package ec.edu.monster.model.enums;

public enum AccountType {
    SAVINGS("Ahorros"),
    CURRENT("Corriente");

    private String label;

    AccountType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
