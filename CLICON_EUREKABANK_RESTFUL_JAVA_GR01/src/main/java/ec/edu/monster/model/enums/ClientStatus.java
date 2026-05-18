package ec.edu.monster.model.enums;

public enum ClientStatus {
    ACTIVE("Activo"),
    INACTIVE("Inactivo");

    private String label;

    ClientStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}