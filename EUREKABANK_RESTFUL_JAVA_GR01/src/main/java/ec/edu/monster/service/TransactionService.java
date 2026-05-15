package ec.edu.monster.service;

import ec.edu.monster.dto.DepositDTO;
import ec.edu.monster.dto.TransactionResponseDTO;
import ec.edu.monster.dto.TransferDTO;
import ec.edu.monster.dto.WithdrawDTO;
import ec.edu.monster.exception.TransactionException;
import ec.edu.monster.model.Account;
import ec.edu.monster.model.Transaction;
import ec.edu.monster.model.TransactionType;
import ec.edu.monster.repository.TransactionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class TransactionService {

    // ── Parameter keys ────────────────────────────────────────────────────────
    private static final String PARAM_TRANSFER_FEE        = "transfer.fee.percentage";
    private static final String PARAM_TRANSFER_DAILY_LIMIT = "transfer.daily.limit";
    private static final String PARAM_WITHDRAW_DAILY_LIMIT = "withdraw.daily.limit";
    private static final String PARAM_MIN_BALANCE          = "account.min.balance";

    @Inject
    private TransactionRepository transactionRepository;

    @Inject
    private AccountService accountService;

    @Inject
    private ParameterService parameterService;

    // ── DEPOSIT ───────────────────────────────────────────────────────────────

    @Transactional
    public TransactionResponseDTO deposit(DepositDTO request) {
        if (request == null) {
            throw new TransactionException("Request body is required", 400);
        }
        if (request.getAccountId() == null) {
            throw new TransactionException("Account id is required", 400);
        }
        validateAmount(request.getAmount());

        Account account = accountService.requireActiveAccount(request.getAccountId());
        account.setBalance(account.getBalance().add(request.getAmount()));

        Transaction transaction = buildTransaction(
                TransactionType.DEPOSIT,
                request.getAmount(),
                BigDecimal.ZERO,
                account,
                null,
                request.getDescription()
        );
        transactionRepository.create(transaction);
        return toResponse(transaction);
    }

    // ── WITHDRAW ──────────────────────────────────────────────────────────────

    @Transactional
    public TransactionResponseDTO withdraw(WithdrawDTO request) {
        if (request == null) {
            throw new TransactionException("Request body is required", 400);
        }
        if (request.getAccountId() == null) {
            throw new TransactionException("Account id is required", 400);
        }
        validateAmount(request.getAmount());

        Account account = accountService.requireActiveAccount(request.getAccountId());

        // 1. Check daily withdraw limit (0 = unlimited)
        BigDecimal withdrawDailyLimit = parseParam(
                parameterService.getValueOrDefault(PARAM_WITHDRAW_DAILY_LIMIT, "0"),
                PARAM_WITHDRAW_DAILY_LIMIT
        );
        if (withdrawDailyLimit.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal todayWithdrawn = getDailyAmount(request.getAccountId(), TransactionType.WITHDRAW);
            if (todayWithdrawn.add(request.getAmount()).compareTo(withdrawDailyLimit) > 0) {
                throw new TransactionException(
                        "Daily withdraw limit exceeded. Limit: " + withdrawDailyLimit
                                + ", already withdrawn today: " + todayWithdrawn, 400
                );
            }
        }

        // 2. Check minimum balance after withdrawal
        BigDecimal minBalance = parseParam(
                parameterService.getValueOrDefault(PARAM_MIN_BALANCE, "0"),
                PARAM_MIN_BALANCE
        );
        BigDecimal balanceAfter = account.getBalance().subtract(request.getAmount());
        if (balanceAfter.compareTo(BigDecimal.ZERO) < 0) {
            throw new TransactionException("Insufficient balance", 400);
        }
        if (balanceAfter.compareTo(minBalance) < 0) {
            throw new TransactionException(
                    "Withdrawal would leave balance below the required minimum of " + minBalance, 400
            );
        }

        account.setBalance(balanceAfter);

        Transaction transaction = buildTransaction(
                TransactionType.WITHDRAW,
                request.getAmount(),
                BigDecimal.ZERO,
                account,
                null,
                request.getDescription()
        );
        transactionRepository.create(transaction);
        return toResponse(transaction);
    }

    // ── TRANSFER ──────────────────────────────────────────────────────────────

    @Transactional
    public TransactionResponseDTO transfer(TransferDTO request) {
        if (request == null) {
            throw new TransactionException("Request body is required", 400);
        }
        if (request.getSourceAccountId() == null || request.getTargetAccountId() == null) {
            throw new TransactionException("Source and target account are required", 400);
        }
        if (request.getSourceAccountId().equals(request.getTargetAccountId())) {
            throw new TransactionException("Source and target accounts must be different", 400);
        }
        validateAmount(request.getAmount());

        Account source = accountService.requireActiveAccount(request.getSourceAccountId());
        Account target = accountService.requireActiveAccount(request.getTargetAccountId());

        // 1. Calculate fee: amount * (feePercentage / 100)
        BigDecimal feePercentage = parseParam(
                parameterService.getValueOrDefault(PARAM_TRANSFER_FEE, "0"),
                PARAM_TRANSFER_FEE
        );
        BigDecimal fee = request.getAmount()
                .multiply(feePercentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal totalDeducted = request.getAmount().add(fee);

        // 2. Check daily transfer limit (0 = unlimited)
        BigDecimal transferDailyLimit = parseParam(
                parameterService.getValueOrDefault(PARAM_TRANSFER_DAILY_LIMIT, "0"),
                PARAM_TRANSFER_DAILY_LIMIT
        );
        if (transferDailyLimit.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal todayTransferred = getDailyAmount(request.getSourceAccountId(), TransactionType.TRANSFER);
            if (todayTransferred.add(request.getAmount()).compareTo(transferDailyLimit) > 0) {
                throw new TransactionException(
                        "Daily transfer limit exceeded. Limit: " + transferDailyLimit
                                + ", already transferred today: " + todayTransferred, 400
                );
            }
        }

        // 3. Verify source has enough balance to cover amount + fee
        if (source.getBalance().compareTo(totalDeducted) < 0) {
            String msg = fee.compareTo(BigDecimal.ZERO) > 0
                    ? "Insufficient balance. Transfer requires " + totalDeducted
                            + " (amount: " + request.getAmount() + " + fee: " + fee + ")"
                    : "Insufficient balance";
            throw new TransactionException(msg, 400);
        }

        // 4. Apply balances atomically
        source.setBalance(source.getBalance().subtract(totalDeducted));
        target.setBalance(target.getBalance().add(request.getAmount()));

        Transaction transaction = buildTransaction(
                TransactionType.TRANSFER,
                request.getAmount(),
                fee,
                source,
                target,
                request.getDescription()
        );
        transactionRepository.create(transaction);
        return toResponse(transaction);
    }

    // ── QUERY ─────────────────────────────────────────────────────────────────

    public List<TransactionResponseDTO> getTransactionsByAccount(Long accountId) {
        if (accountId == null) {
            throw new TransactionException("Account id is required", 400);
        }
        accountService.requireAccount(accountId);
        return transactionRepository.findByAccountId(accountId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── PRIVATE HELPERS ───────────────────────────────────────────────────────

    private Transaction buildTransaction(TransactionType type, BigDecimal amount, BigDecimal fee,
            Account source, Account target, String description) {
        Transaction transaction = new Transaction();
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setFee(fee);
        transaction.setDate(LocalDateTime.now());
        transaction.setSourceAccount(source);
        transaction.setTargetAccount(target);
        transaction.setDescription(normalize(description));
        return transaction;
    }

    private TransactionResponseDTO toResponse(Transaction transaction) {
        Long targetId = transaction.getTargetAccount() == null
                ? null
                : transaction.getTargetAccount().getId();
        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getFee(),
                transaction.getDate(),
                transaction.getSourceAccount().getId(),
                targetId,
                transaction.getDescription()
        );
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionException("Amount must be greater than zero", 400);
        }
    }

    private BigDecimal getDailyAmount(Long accountId, TransactionType type) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return transactionRepository.sumDailyAmount(accountId, type, startOfDay, endOfDay);
    }

    private BigDecimal parseParam(String value, String key) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            throw new TransactionException("Invalid system parameter value for: " + key, 500);
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
