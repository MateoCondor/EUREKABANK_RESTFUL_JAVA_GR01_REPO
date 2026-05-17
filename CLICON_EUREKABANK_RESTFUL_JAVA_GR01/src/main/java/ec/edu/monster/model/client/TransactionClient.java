package ec.edu.monster.model.client;

import java.util.List;
import java.util.Map;

import ec.edu.monster.model.dto.DepositRequest;
import ec.edu.monster.model.dto.TransferRequest;
import ec.edu.monster.model.dto.WithdrawRequest;
import ec.edu.monster.model.entity.Transaction;
import ec.edu.monster.model.service.TransactionService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransactionClient {
    private final TransactionService service;

    public List<Transaction> getByAccount(Long accountId) {
        return ApiExecutor.execute(service.getByAccount(accountId), Map.of(
                404, "La cuenta no existe"));
    }

    public Transaction deposit(DepositRequest dto) {
        return ApiExecutor.execute(service.deposit(dto), Map.of(
                404, "No se encontró la cuenta"));
    }

    public Transaction withdraw(WithdrawRequest dto) {
        return ApiExecutor.execute(service.withdraw(dto), Map.of(
                400, "No se puede retirar la cantidad especificada",
                404, "No se encontró la cuenta o está inactiva"));
    }

    public Transaction transfer(TransferRequest dto) {
        return ApiExecutor.execute(service.transfer(dto), Map.of(
                400, "No se puede transferir la cantidad especificada",
                404, "No se encontró la cuenta de origen o de destino o están inactivas"));
    }
}
