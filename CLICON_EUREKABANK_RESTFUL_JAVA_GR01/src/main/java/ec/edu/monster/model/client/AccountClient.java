package ec.edu.monster.model.client;

import java.util.List;
import java.util.Map;

import ec.edu.monster.model.dto.AccountBalanceResponse;
import ec.edu.monster.model.dto.AccountRequest;
import ec.edu.monster.model.dto.AccountStatusRequest;
import ec.edu.monster.model.entity.Account;
import ec.edu.monster.model.service.AccountService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccountClient {
    private final AccountService service;

    public List<Account> getAll() {
        return ApiExecutor.execute(service.getAll());
    }

    public Account getById(Long id) {
        return ApiExecutor.execute(service.getById(id), Map.of(404, "La cuenta no existe"));
    }

    public AccountBalanceResponse getBalance(Long id) {
        return ApiExecutor.execute(service.getBalance(id), Map.of(404, "La cuenta no existe"));
    }

    public List<Account> getByClientId(Long clientId) {
        return ApiExecutor.execute(service.getByClientId(clientId), Map.of(404, "El cliente no existe"));
    }

    public Account create(AccountRequest dto) {
        return ApiExecutor.execute(service.create(dto), Map.of(404, "El cliente no existe"));
    }

    public Account updateStatus(Long id, AccountStatusRequest dto) {
        return ApiExecutor.execute(service.updateStatus(id, dto), Map.of(404, "La cuenta no existe"));
    }
}
