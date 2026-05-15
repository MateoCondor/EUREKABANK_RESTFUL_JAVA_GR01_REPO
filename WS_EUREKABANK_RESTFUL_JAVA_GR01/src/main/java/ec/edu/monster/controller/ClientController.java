package ec.edu.monster.controller;

import ec.edu.monster.dto.ClientRequestDTO;
import ec.edu.monster.dto.ClientResponseDTO;
import ec.edu.monster.service.ClientService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/clients")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClientController {

    @Inject
    private ClientService clientService;

    @GET
    public Response getAll() {
        List<ClientResponseDTO> clients = clientService.getAllClients();
        return Response.ok(clients).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        ClientResponseDTO client = clientService.getClientById(id);
        return Response.ok(client).build();
    }

    @GET
    @Path("/dni/{dni}")
    public Response getByDni(@PathParam("dni") String dni) {
        ClientResponseDTO client = clientService.findByDni(dni);
        return Response.ok(client).build();
    }

    @POST
    public Response create(ClientRequestDTO request) {
        ClientResponseDTO created = clientService.createClient(request);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, ClientRequestDTO request) {
        ClientResponseDTO updated = clientService.updateClient(id, request);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        clientService.deleteClient(id);
        return Response.ok(Map.of("message", "Client deleted")).build();
    }
}
