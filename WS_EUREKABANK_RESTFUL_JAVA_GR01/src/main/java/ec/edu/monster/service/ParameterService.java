package ec.edu.monster.service;

import ec.edu.monster.dto.ParameterDTO;
import ec.edu.monster.exception.ParameterException;
import ec.edu.monster.model.Parameter;
import ec.edu.monster.repository.ParameterRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ParameterService {

    @Inject
    private ParameterRepository parameterRepository;

    public List<ParameterDTO> getAllParameters() {
        return parameterRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ParameterDTO getByKey(String key) {
        if (key == null || key.isBlank()) {
            throw new ParameterException("Parameter key is required", 400);
        }
        Parameter parameter = parameterRepository.findByKey(key.trim())
                .orElseThrow(() -> new ParameterException("Parameter not found: " + key, 404));
        return toDTO(parameter);
    }

    @Transactional
    public ParameterDTO createParameter(ParameterDTO request) {
        validateRequest(request);
        String normalizedKey = request.getKey().trim();

        if (parameterRepository.findByKey(normalizedKey).isPresent()) {
            throw new ParameterException("Parameter already exists with key: " + normalizedKey, 409);
        }

        Parameter parameter = new Parameter();
        parameter.setKey(normalizedKey);
        parameter.setValue(request.getValue().trim());
        parameter.setDescription(normalize(request.getDescription()));

        parameterRepository.create(parameter);
        return toDTO(parameter);
    }

    @Transactional
    public ParameterDTO updateParameter(Long id, ParameterDTO request) {
        if (id == null) {
            throw new ParameterException("Parameter id is required", 400);
        }
        if (request == null || request.getValue() == null || request.getValue().isBlank()) {
            throw new ParameterException("Parameter value is required", 400);
        }

        Parameter parameter = parameterRepository.findById(id)
                .orElseThrow(() -> new ParameterException("Parameter not found", 404));

        parameter.setValue(request.getValue().trim());
        parameter.setDescription(normalize(request.getDescription()));

        Parameter updated = parameterRepository.update(parameter);
        return toDTO(updated);
    }

    /**
     * Returns the raw String value of a parameter by key.
     * Intended for internal use by other services (e.g. TransactionService).
     *
     * @throws ParameterException if the key does not exist.
     */
    public String requireValue(String key) {
        return parameterRepository.findByKey(key)
                .orElseThrow(() -> new ParameterException("Required system parameter not configured: " + key, 500))
                .getValue();
    }

    /**
     * Returns the value of a parameter by key, or the given default if not configured.
     * Allows graceful degradation when parameters are optional.
     */
    public String getValueOrDefault(String key, String defaultValue) {
        return parameterRepository.findByKey(key)
                .map(Parameter::getValue)
                .orElse(defaultValue);
    }

    private void validateRequest(ParameterDTO request) {
        if (request == null) {
            throw new ParameterException("Request body is required", 400);
        }
        if (request.getKey() == null || request.getKey().isBlank()) {
            throw new ParameterException("Parameter key is required", 400);
        }
        if (request.getValue() == null || request.getValue().isBlank()) {
            throw new ParameterException("Parameter value is required", 400);
        }
    }

    private ParameterDTO toDTO(Parameter parameter) {
        return new ParameterDTO(
                parameter.getId(),
                parameter.getKey(),
                parameter.getValue(),
                parameter.getDescription()
        );
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
