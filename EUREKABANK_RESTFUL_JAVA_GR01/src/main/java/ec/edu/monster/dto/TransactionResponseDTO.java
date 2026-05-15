package ec.edu.monster.dto;

import ec.edu.monster.model.TransactionType;
import ec.edu.monster.model.TransferType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponseDTO {

    private Long id;
    private TransactionType type;
    private TransferType transferType;
    private BigDecimal amount;
    private BigDecimal fee;
    private LocalDateTime date;
    private Long sourceAccountId;
    private Long targetAccountId;
    private String description;

    public TransactionResponseDTO() {
    }

    public TransactionResponseDTO(Long id, TransactionType type, TransferType transferType,
            BigDecimal amount, BigDecimal fee, LocalDateTime date,
            Long sourceAccountId, Long targetAccountId, String description) {
        this.id = id;
        this.type = type;
        this.transferType = transferType;
        this.amount = amount;
        this.fee = fee;
        this.date = date;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransferType getTransferType() {
        return transferType;
    }

    public void setTransferType(TransferType transferType) {
        this.transferType = transferType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Long getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(Long sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public Long getTargetAccountId() {
        return targetAccountId;
    }

    public void setTargetAccountId(Long targetAccountId) {
        this.targetAccountId = targetAccountId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
