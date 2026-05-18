package ec.edu.monster.model.entity;

import java.math.BigDecimal;

import ec.edu.monster.model.enums.AccountStatus;
import ec.edu.monster.model.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
        Long id;
        String accountNumber;
        BigDecimal balance;
        AccountStatus status;
        AccountType type;
        Long clientId;
}
