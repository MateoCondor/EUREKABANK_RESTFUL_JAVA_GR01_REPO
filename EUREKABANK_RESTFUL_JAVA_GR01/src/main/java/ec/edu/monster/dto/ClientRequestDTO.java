package ec.edu.monster.dto;

import ec.edu.monster.model.ClientStatus;

public class ClientRequestDTO {

    private String name;
    private String dni;
    private String email;
    private String phone;
    private ClientStatus status;
    // Credentials for the auto-created user
    private String username;
    private String password;

    public ClientRequestDTO() {
    }

    public ClientRequestDTO(String name, String dni, String email, String phone,
            ClientStatus status, String username, String password) {
        this.name = name;
        this.dni = dni;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.username = username;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ClientStatus getStatus() {
        return status;
    }

    public void setStatus(ClientStatus status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
