package ec.edu.monster.dto;

import ec.edu.monster.model.AccountStatus;
import ec.edu.monster.model.AccountType;
import java.math.BigDecimal;

public class AccountResponseDTO {

    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private AccountStatus status;
    private AccountType type;
    private Long clientId;

    public AccountResponseDTO() {
    }

    public AccountResponseDTO(Long id, String accountNumber, BigDecimal balance,
            AccountStatus status, AccountType type, Long clientId) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.status = status;
        this.type = type;
        this.clientId = clientId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
