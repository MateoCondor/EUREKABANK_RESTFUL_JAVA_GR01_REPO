package ec.edu.monster.model.enums;

public enum AccountStatus {
    ACTIVE("Activa"),
    BLOCKED("Bloqueada"),
    CLOSED("Cerrada");

    private String label;

    AccountStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
