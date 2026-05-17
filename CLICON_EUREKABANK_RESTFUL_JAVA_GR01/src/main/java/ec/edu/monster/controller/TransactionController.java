package ec.edu.monster.controller;

import java.util.List;

import ec.edu.monster.model.client.TransactionClient;
import ec.edu.monster.model.dto.DepositRequest;
import ec.edu.monster.model.dto.TransferRequest;
import ec.edu.monster.model.dto.WithdrawRequest;
import ec.edu.monster.model.entity.Transaction;
import ec.edu.monster.model.exceptions.ApiException;
import ec.edu.monster.view.TransactionView;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransactionController {
    private final TransactionView view;
    private final TransactionClient client;

    public void run() {
        boolean inModule = true;
        while (inModule) {
            int option = view.showSubMenu();
            try {
                switch (option) {
                    case 1 -> getByAccount();
                    case 2 -> deposit();
                    case 3 -> withdraw();
                    case 4 -> transfer();
                    case 0 -> inModule = false;
                }
            } catch (ApiException e) {
                view.showError(e.getMessage());
            }
        }
    }

    private void getByAccount() {
        Long accountId = view.askAccountId();
        List<Transaction> history = client.getByAccount(accountId);
        view.showHistory(history);
    }

    private void deposit() {
        DepositRequest request = view.askDepositData();
        Transaction result = client.deposit(request);
        view.showReceipt(result, "Depósito procesado de forma exitosa.");
    }

    private void withdraw() {
        WithdrawRequest request = view.askWithdrawData();
        Transaction result = client.withdraw(request);
        view.showReceipt(result, "Retiro procesado de forma exitosa.");
    }

    private void transfer() {
        TransferRequest request = view.askTransferData();
        Transaction result = client.transfer(request);
        view.showReceipt(result, "Transferencia procesada de forma exitosa.");
    }
}
