package ec.edu.monster.model.client;

import java.util.Map;

import ec.edu.monster.model.dto.LoginRequest;
import ec.edu.monster.model.dto.LoginResponse;
import ec.edu.monster.model.entity.UserSession;
import ec.edu.monster.model.service.AuthService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthClient {
    private final AuthService service;

    public LoginResponse login(String username, String password) {
        LoginRequest dto = new LoginRequest(username, password);
        LoginResponse response = ApiExecutor.execute(service.login(dto), Map.of(
                400, "Usuario o contraseña incorrectos.",
                401, "Usuario o contraseña incorrectos.",
                403, "Tu cuenta no tiene permisos para acceder aquí."));
        UserSession userSession = UserSession.getInstance();
        userSession.saveSession(response.token(), response.username(), response.role());
        return response;
    }
}
