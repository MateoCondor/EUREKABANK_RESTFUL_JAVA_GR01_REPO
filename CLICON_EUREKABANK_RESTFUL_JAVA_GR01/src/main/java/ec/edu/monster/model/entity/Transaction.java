package ec.edu.monster.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import ec.edu.monster.model.enums.TransactionType;
import ec.edu.monster.model.enums.TransferType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {
    private Long id;
    private TransactionType type;
    private TransferType transferType;
    private BigDecimal amount;
    private BigDecimal fee;
    private LocalDateTime date;
    private Long sourceAccountId;
    private Long targetAccountId;
    private String description;
}
