package ec.edu.monster.model.dto;

import java.math.BigDecimal;

import ec.edu.monster.model.enums.TransferType;

public record TransferRequest(
                Long sourceAccountId,
                Long targetAccountId,
                BigDecimal amount,
                String description,
                TransferType transferType) {

}
