package ec.edu.monster.service;

import ec.edu.monster.dto.ClientRequestDTO;
import ec.edu.monster.dto.ClientResponseDTO;
import ec.edu.monster.exception.ClientException;
import ec.edu.monster.model.Client;
import ec.edu.monster.model.ClientStatus;
import ec.edu.monster.model.User;
import ec.edu.monster.model.UserRole;
import ec.edu.monster.model.UserStatus;
import ec.edu.monster.repository.ClientRepository;
import ec.edu.monster.repository.UserRepository;
import ec.edu.monster.security.PasswordUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ApplicationScoped
public class ClientService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Inject
    private ClientRepository clientRepository;

    @Inject
    private UserRepository userRepository;

    /**
     * Creates a client and its associated user account atomically.
     * Every client must have a user; username and password are required.
     */
    @Transactional
    public ClientResponseDTO createClient(ClientRequestDTO request) {
        if (request == null) {
            throw new ClientException("Request body is required", 400);
        }

        String name = normalize(request.getName());
        String dni = normalize(request.getDni());
        String email = normalize(request.getEmail());
        String phone = normalize(request.getPhone());
        String username = normalize(request.getUsername());
        String password = request.getPassword();
        ClientStatus status = request.getStatus() == null ? ClientStatus.ACTIVE : request.getStatus();

        // Validate client fields
        validateRequired("Name", name);
        validateRequired("DNI", dni);
        validateRequired("Email", email);
        validateEmail(email);

        // Validate user credentials
        validateRequired("Username", username);
        validateRequired("Password", password);
        if (password.length() < 6) {
            throw new ClientException("Password must be at least 6 characters", 400);
        }

        // Check uniqueness
        if (clientRepository.findByDni(dni).isPresent()) {
            throw new ClientException("DNI already exists", 409);
        }
        if (userRepository.existsByUsername(username)) {
            throw new ClientException("Username already exists", 409);
        }

        // 1. Create User first (Client holds the FK)
        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtil.hashPassword(password));
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.create(user);

        // 2. Create Client linked to the new User
        Client client = new Client();
        client.setName(name);
        client.setDni(dni);
        client.setEmail(email);
        client.setPhone(phone);
        client.setStatus(status);
        client.setUser(user);
        clientRepository.create(client);

        return toResponse(client);
    }

    @Transactional
    public ClientResponseDTO updateClient(Long id, ClientRequestDTO request) {
        if (id == null) {
            throw new ClientException("Client id is required", 400);
        }
        if (request == null) {
            throw new ClientException("Request body is required", 400);
        }

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientException("Client not found", 404));

        String name = normalize(request.getName());
        String dni = normalize(request.getDni());
        String email = normalize(request.getEmail());
        String phone = normalize(request.getPhone());
        ClientStatus status = request.getStatus() == null ? client.getStatus() : request.getStatus();

        validateRequired("Name", name);
        validateRequired("DNI", dni);
        validateRequired("Email", email);
        validateEmail(email);

        if (!Objects.equals(client.getDni(), dni)) {
            if (clientRepository.findByDni(dni).isPresent()) {
                throw new ClientException("DNI already exists", 409);
            }
        }

        client.setName(name);
        client.setDni(dni);
        client.setEmail(email);
        client.setPhone(phone);
        client.setStatus(status);

        Client updated = clientRepository.update(client);
        return toResponse(updated);
    }

    public ClientResponseDTO getClientById(Long id) {
        if (id == null) {
            throw new ClientException("Client id is required", 400);
        }
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientException("Client not found", 404));
        return toResponse(client);
    }

    public List<ClientResponseDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ClientResponseDTO findByDni(String dni) {
        String normalized = normalize(dni);
        validateRequired("DNI", normalized);
        Client client = clientRepository.findByDni(normalized)
                .orElseThrow(() -> new ClientException("Client not found", 404));
        return toResponse(client);
    }

    @Transactional
    public void deleteClient(Long id) {
        if (id == null) {
            throw new ClientException("Client id is required", 400);
        }
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientException("Client not found", 404));
        clientRepository.delete(client);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void validateRequired(String field, String value) {
        if (isBlank(value)) {
            throw new ClientException(field + " is required", 400);
        }
    }

    private void validateEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ClientException("Email is invalid", 400);
        }
    }

    private ClientResponseDTO toResponse(Client client) {
        return new ClientResponseDTO(
                client.getId(),
                client.getName(),
                client.getDni(),
                client.getEmail(),
                client.getPhone(),
                client.getStatus(),
                client.getUser().getId(),
                client.getUser().getUsername());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
