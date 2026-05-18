package ec.edu.monster.controller;

import java.math.BigDecimal;
import java.util.List;
import ec.edu.monster.model.client.AccountClient;
import ec.edu.monster.model.dto.AccountRequest;
import ec.edu.monster.model.dto.AccountStatusRequest;
import ec.edu.monster.model.entity.Account;
import ec.edu.monster.model.exceptions.ApiException;
import ec.edu.monster.view.AccountView;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccountController {
    private final AccountView view;
    private final AccountClient client;

    public void run() {
        boolean inModule = true;
        while (inModule) {
            int option = view.showSubMenu();
            try {
                switch (option) {
                    case 1 -> getAll();
                    case 2 -> getById();
                    case 3 -> getBalance();
                    case 4 -> getByClient();
                    case 5 -> create();
                    case 6 -> updateStatus();
                    case 0 -> inModule = false;
                }
            } catch (ApiException e) {
                view.showError(e.getMessage());
            }
        }
    }

    private void getAll() {
        List<Account> list = client.getAll();
        view.showAccounts(list);
    }

    private void getById() {
        Long id = view.askAccountId("consultar detalles");
        Account a = client.getById(id);
        view.showAccountDetails(a);
    }

    private void getBalance() {
        Long id = view.askAccountId("verificar el saldo");
        BigDecimal balance = client.getBalance(id).balance();
        view.showBalance(balance);
    }

    private void getByClient() {
        Long clientId = view.askClientId();
        List<Account> list = client.getByClientId(clientId);
        view.showAccounts(list);
    }

    private void create() {
        AccountRequest request = view.askAccountData();
        Account newAccount = client.create(request);
        view.showSuccess("Cuenta bancaria abierta con éxito. Nro: " + newAccount.getAccountNumber());
    }

    private void updateStatus() {
        Long id = view.askAccountId("modificar el estado");

        // Buscamos la cuenta actual para conocer su estado previo antes de preguntar
        Account actual = client.getById(id);
        AccountStatusRequest request = view.askStatusUpdate(actual.getStatus());

        Account updated = client.updateStatus(id, request);
        view.showSuccess(
                "Estado de la cuenta " + updated.getAccountNumber() + " actualizado a: " + updated.getStatus());
    }
}
