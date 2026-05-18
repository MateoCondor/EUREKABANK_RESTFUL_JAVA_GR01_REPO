package ec.edu.monster.model.dto;

import ec.edu.monster.model.enums.UserRole;

public record LoginResponse(
        UserRole role,
        String token,
        String username) {
}
