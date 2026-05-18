package ec.edu.monster.model.dto;

import ec.edu.monster.model.enums.AccountStatus;

public record AccountStatusRequest(
        AccountStatus status) {

}
