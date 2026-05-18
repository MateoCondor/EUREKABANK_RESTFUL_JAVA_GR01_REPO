package ec.edu.monster.model.entity;

import ec.edu.monster.model.enums.ClientStatus;
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
public class Client {
    private Long id;
    private String name;
    private String dni;
    private String email;
    private String phone;
    private ClientStatus status;
    private Long userId;
    private String username;
}
