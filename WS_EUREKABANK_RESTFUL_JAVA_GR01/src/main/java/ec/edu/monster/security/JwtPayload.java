package ec.edu.monster.security;

public class JwtPayload {

    private final String username;
    private final String role;
    private final long expiresAt;

    public JwtPayload(String username, String role, long expiresAt) {
        this.username = username;
        this.role = role;
        this.expiresAt = expiresAt;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public long getExpiresAt() {
        return expiresAt;
    }
}
