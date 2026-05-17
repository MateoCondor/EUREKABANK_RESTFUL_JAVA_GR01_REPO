package ec.edu.monster.model.dto;

import ec.edu.monster.model.enums.AccountStatus;
import ec.edu.monster.model.enums.AccountType;

public record AccountRequest(Long clientId,
        AccountType type,
        AccountStatus status) {

}
