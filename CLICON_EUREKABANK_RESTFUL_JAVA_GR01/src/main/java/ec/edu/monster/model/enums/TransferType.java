package ec.edu.monster.model.enums;

public enum TransferType {
    CREDIT("Crédito"),
    DEBIT("Débito");

    private String label;

    TransferType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
