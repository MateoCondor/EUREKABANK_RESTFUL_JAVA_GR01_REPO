package ec.edu.monster.controller;

import ec.edu.monster.dto.DepositDTO;
import ec.edu.monster.dto.TransactionResponseDTO;
import ec.edu.monster.dto.TransferDTO;
import ec.edu.monster.dto.WithdrawDTO;
import ec.edu.monster.service.TransactionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/transactions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TransactionController {

    @Inject
    private TransactionService transactionService;

    @POST
    @Path("/deposit")
    public Response deposit(DepositDTO request) {
        TransactionResponseDTO response = transactionService.deposit(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @POST
    @Path("/withdraw")
    public Response withdraw(WithdrawDTO request) {
        TransactionResponseDTO response = transactionService.withdraw(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @POST
    @Path("/transfer")
    public Response transfer(TransferDTO request) {
        TransactionResponseDTO response = transactionService.transfer(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Path("/account/{accountId}")
    public Response getByAccount(@PathParam("accountId") Long accountId) {
        List<TransactionResponseDTO> transactions = transactionService.getTransactionsByAccount(accountId);
        return Response.ok(transactions).build();
    }
}
