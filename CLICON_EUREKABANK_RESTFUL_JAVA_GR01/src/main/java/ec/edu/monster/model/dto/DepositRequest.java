package ec.edu.monster.model.dto;

import java.math.BigDecimal;

public record DepositRequest(
        Long accountId,
        BigDecimal amount,
        String description) {

}
