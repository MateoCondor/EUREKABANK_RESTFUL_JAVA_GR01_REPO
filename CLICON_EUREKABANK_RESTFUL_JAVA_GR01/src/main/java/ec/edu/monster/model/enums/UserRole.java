package ec.edu.monster.model.enums;

public enum UserRole {
    ADMIN("Administrador"),
    USER("Cliente");

    private String label;

    UserRole(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
