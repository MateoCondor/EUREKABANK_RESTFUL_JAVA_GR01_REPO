package ec.edu.monster.service;

import ec.edu.monster.dto.AccountRequestDTO;
import ec.edu.monster.dto.AccountResponseDTO;
import ec.edu.monster.exception.AccountException;
import ec.edu.monster.model.Account;
import ec.edu.monster.model.AccountStatus;
import ec.edu.monster.model.AccountType;
import ec.edu.monster.model.Client;
import ec.edu.monster.repository.AccountRepository;
import ec.edu.monster.repository.ClientRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class AccountService {

    private static final int ACCOUNT_NUMBER_LENGTH = 12;

    @Inject
    private AccountRepository accountRepository;

    @Inject
    private ClientRepository clientRepository;

    @Transactional
    public AccountResponseDTO createAccount(AccountRequestDTO request) {
        if (request == null) {
            throw new AccountException("Request body is required", 400);
        }
        if (request.getClientId() == null) {
            throw new AccountException("Client id is required", 400);
        }
        if (request.getType() == null) {
            throw new AccountException("Account type is required", 400);
        }

        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new AccountException("Client not found", 404));

        Account account = new Account();
        account.setClient(client);
        account.setType(request.getType());
        account.setStatus(AccountStatus.ACTIVE);
        account.setBalance(BigDecimal.ZERO);
        account.setAccountNumber(generateUniqueAccountNumber());

        accountRepository.create(account);
        return toResponse(account);
    }

    public AccountResponseDTO getAccountById(Long id) {
        if (id == null) {
            throw new AccountException("Account id is required", 400);
        }
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountException("Account not found", 404));
        return toResponse(account);
    }

    public List<AccountResponseDTO> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<AccountResponseDTO> getAccountsByClient(Long clientId) {
        if (clientId == null) {
            throw new AccountException("Client id is required", 400);
        }
        clientRepository.findById(clientId)
                .orElseThrow(() -> new AccountException("Client not found", 404));

        return accountRepository.findByClientId(clientId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AccountResponseDTO updateStatus(Long id, AccountStatus status) {
        if (id == null) {
            throw new AccountException("Account id is required", 400);
        }
        if (status == null) {
            throw new AccountException("Account status is required", 400);
        }

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountException("Account not found", 404));
        account.setStatus(status);
        Account updated = accountRepository.update(account);
        return toResponse(updated);
    }

    public BigDecimal getBalance(Long id) {
        if (id == null) {
            throw new AccountException("Account id is required", 400);
        }
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountException("Account not found", 404));
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountException("Account is not active", 400);
        }
        return account.getBalance();
    }

    /**
     * Returns the Account entity if it exists and is ACTIVE.
     * Used internally by TransactionService to operate within the same JPA transaction.
     */
    public Account requireActiveAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountException("Account not found", 404));
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountException("Account is not active", 400);
        }
        return account;
    }

    /**
     * Returns the Account entity if it exists (any status).
     * Used internally by TransactionService to verify account existence.
     */
    public Account requireAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountException("Account not found", 404));
    }

    private AccountResponseDTO toResponse(Account account) {
        return new AccountResponseDTO(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getStatus(),
                account.getType(),
                account.getClient().getId()
        );
    }

    private String generateUniqueAccountNumber() {
        for (int attempt = 0; attempt < 10; attempt++) {
            String candidate = generateAccountNumber();
            if (accountRepository.findByAccountNumber(candidate).isEmpty()) {
                return candidate;
            }
        }
        throw new AccountException("Unable to generate account number", 400);
    }

    private String generateAccountNumber() {
        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder(ACCOUNT_NUMBER_LENGTH);
        for (int i = 0; i < ACCOUNT_NUMBER_LENGTH; i++) {
            builder.append(random.nextInt(10));
        }
        return builder.toString();
    }
}
