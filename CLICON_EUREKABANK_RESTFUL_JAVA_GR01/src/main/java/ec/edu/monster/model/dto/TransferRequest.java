package ec.edu.monster.model.dto;

import java.math.BigDecimal;

public record TransferRequest(
        Long sourceAccountId,
        Long targetAccountId,
        BigDecimal amount,
        String description) {

}
