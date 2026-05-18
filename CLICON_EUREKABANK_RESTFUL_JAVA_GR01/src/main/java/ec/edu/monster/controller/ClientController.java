package ec.edu.monster.controller;

import java.util.List;

import ec.edu.monster.model.client.ClientClient;
import ec.edu.monster.model.dto.ClientRequest;
import ec.edu.monster.model.entity.Client;
import ec.edu.monster.model.exceptions.ApiException;
import ec.edu.monster.view.ClientView;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClientController {
    private final ClientView view;
    private final ClientClient client;

    public void run() {
        boolean inModule = true;
        while (inModule) {
            int option = view.showSubMenu();
            try {
                switch (option) {
                    case 1 -> getAll();
                    case 2 -> getById();
                    case 3 -> getByDni();
                    case 4 -> create();
                    case 5 -> update();
                    case 6 -> delete();
                    case 0 -> inModule = false;
                }
            } catch (ApiException e) {
                view.showError(e.getMessage());
            }
        }
    }

    private void getAll() {
        List<Client> list = client.getAll();
        view.showClients(list);
    }

    private void getById() {
        Long id = view.askId("buscar");
        if (id == null)
            return;

        Client c = client.getById(id);
        view.showClientDetails(c);
    }

    private void getByDni() {
        String dni = view.askDni();
        if (dni.isBlank())
            return;

        Client c = client.getByDni(dni);
        view.showClientDetails(c);
    }

    private void create() {
        ClientRequest request = view.askClientData(null);
        Client newClient = client.create(request);
        view.showSuccess("Cliente registrado con ID: " + newClient.getId());
    }

    private void update() {
        Long id = view.askId("actualizar");
        if (id == null)
            return;

        Client currentClient = client.getById(id);
        ClientRequest request = view.askClientData(currentClient);

        client.update(id, request);
        view.showSuccess("Cliente actualizado correctamente.");
    }

    private void delete() {
        Long id = view.askId("eliminar");
        if (id == null)
            return;

        client.delete(id);
        view.showSuccess("Cliente eliminado correctamente.");
    }
}