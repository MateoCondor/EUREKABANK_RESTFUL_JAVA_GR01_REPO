package ec.edu.monster.model.client;

import java.util.List;
import java.util.Map;

import ec.edu.monster.model.dto.ClientRequest;
import ec.edu.monster.model.entity.Client;
import ec.edu.monster.model.service.ClientService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClientClient {
    private final ClientService service;

    public List<Client> getAll() {
        return ApiExecutor.execute(service.getAll());
    }

    public Client getById(Long id) {
        return ApiExecutor.execute(service.getById(id), Map.of(404, "El cliente no existe"));
    }

    public Client getByDni(String dni) {
        return ApiExecutor.execute(service.getByDni(dni), Map.of(404, "No se pudo encontrar el cliente por el DNI"));
    }

    public Client create(ClientRequest request) {
        return ApiExecutor.execute(service.create(request), Map.of(409, "El DNI o nombre de usuario ya existe"));
    }

    public Client update(Long id, ClientRequest request) {
        return ApiExecutor.execute(service.update(id, request), Map.of(
                404, "El cliente no existe",
                409, "El DNI ya esta siendo utilizado"));
    }

    public void delete(Long id) {
        ApiExecutor.execute(service.delete(id), Map.of(404, "El cliente no existe"));
    }
}
