package ec.edu.monster.model.entity;

import ec.edu.monster.model.enums.UserRole;
import lombok.AccessLevel;
import lombok.Getter;

@Getter
public class UserSession {
    @Getter(value = AccessLevel.NONE)
    private static UserSession instance;
    private String token;
    private String username;
    private UserRole role;

    private UserSession() {
    }

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }

        return instance;
    }

    public void saveSession(String token, String username, UserRole role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }

    public void clear() {
        token = null;
        username = null;
        role = null;
    }

    public boolean isLoggedIn() {
        return token != null;
    }
}
