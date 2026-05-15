package ec.edu.monster.controller;

import ec.edu.monster.dto.AccountRequestDTO;
import ec.edu.monster.dto.AccountResponseDTO;
import ec.edu.monster.model.AccountStatus;
import ec.edu.monster.service.AccountService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
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

@Path("/accounts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AccountController {

    @Inject
    private AccountService accountService;

    @GET
    public Response getAll() {
        List<AccountResponseDTO> accounts = accountService.getAllAccounts();
        return Response.ok(accounts).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        AccountResponseDTO account = accountService.getAccountById(id);
        return Response.ok(account).build();
    }

    @GET
    @Path("/client/{clientId}")
    public Response getByClient(@PathParam("clientId") Long clientId) {
        List<AccountResponseDTO> accounts = accountService.getAccountsByClient(clientId);
        return Response.ok(accounts).build();
    }

    @POST
    public Response create(AccountRequestDTO request) {
        AccountResponseDTO created = accountService.createAccount(request);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}/status")
    public Response updateStatus(@PathParam("id") Long id, AccountRequestDTO request) {
        AccountStatus status = request == null ? null : request.getStatus();
        AccountResponseDTO updated = accountService.updateStatus(id, status);
        return Response.ok(updated).build();
    }

    @GET
    @Path("/{id}/balance")
    public Response getBalance(@PathParam("id") Long id) {
        return Response.ok(Map.of("balance", accountService.getBalance(id))).build();
    }
}
