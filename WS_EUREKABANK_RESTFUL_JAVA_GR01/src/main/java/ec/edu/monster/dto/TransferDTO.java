package ec.edu.monster.dto;

import ec.edu.monster.model.TransferType;
import java.math.BigDecimal;

public class TransferDTO {

    private Long sourceAccountId;
    private Long targetAccountId;
    private BigDecimal amount;
    private String description;
    /**
     * Required. CREDIT = source pushes money to target (wire transfer).
     *           DEBIT  = target pulls money from source (direct debit).
     */
    private TransferType transferType;

    public TransferDTO() {
    }

    public TransferDTO(Long sourceAccountId, Long targetAccountId, BigDecimal amount,
            String description, TransferType transferType) {
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
        this.description = description;
        this.transferType = transferType;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TransferType getTransferType() {
        return transferType;
    }

    public void setTransferType(TransferType transferType) {
        this.transferType = transferType;
    }
}
