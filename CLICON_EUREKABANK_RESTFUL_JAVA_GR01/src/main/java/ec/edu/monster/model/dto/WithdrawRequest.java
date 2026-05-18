package ec.edu.monster.model.dto;

import java.math.BigDecimal;

public record WithdrawRequest(
        Long accountId,
        BigDecimal amount,
        String description) {

}
