package ec.edu.monster.model.enums;

public enum TransactionType {
    DEPOSIT("Depósito"),
    WITHDRAW("Retiro"),
    TRANSFER("Transferencia");

    private String label;

    TransactionType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}