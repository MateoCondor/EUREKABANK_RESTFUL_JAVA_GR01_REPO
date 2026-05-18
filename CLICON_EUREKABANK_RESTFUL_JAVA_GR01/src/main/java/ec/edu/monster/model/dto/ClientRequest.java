package ec.edu.monster.model.dto;

import ec.edu.monster.model.enums.ClientStatus;

public record ClientRequest(
        String name,
        String dni,
        String email,
        String phone,
        ClientStatus status,
        String username,
        String password) {

}
