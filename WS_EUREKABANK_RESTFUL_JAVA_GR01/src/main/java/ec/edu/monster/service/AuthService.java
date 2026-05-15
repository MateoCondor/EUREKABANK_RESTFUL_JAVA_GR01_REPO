package ec.edu.monster.service;

import ec.edu.monster.dto.LoginRequestDTO;
import ec.edu.monster.dto.LoginResponseDTO;
import ec.edu.monster.exception.AuthException;
import ec.edu.monster.model.User;
import ec.edu.monster.model.UserStatus;
import ec.edu.monster.repository.UserRepository;
import ec.edu.monster.security.JwtPayload;
import ec.edu.monster.security.JwtUtil;
import ec.edu.monster.security.PasswordUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AuthService {

    @Inject
    private UserRepository userRepository;

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {
        if (request == null) {
            throw new AuthException("Request body is required", 400);
        }

        String username = normalize(request.getUsername());
        String password = request.getPassword();

        if (isBlank(username) || isBlank(password)) {
            throw new AuthException("Username and password are required", 400);
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthException("User not found", 404));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AuthException("User is inactive", 400);
        }

        if (!validateUser(user, password)) {
            throw new AuthException("Invalid credentials", 400);
        }

        String token = JwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new LoginResponseDTO(token, user.getUsername(), user.getRole().name());
    }

    public boolean validateUser(User user, String rawPassword) {
        if (user == null || isBlank(rawPassword)) {
            return false;
        }
        return PasswordUtil.verifyPassword(rawPassword, user.getPassword());
    }

    public JwtPayload validateToken(String token) {
        return JwtUtil.validateToken(token);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
