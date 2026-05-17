package ec.edu.monster;

import ec.edu.monster.controller.AccountController;
import ec.edu.monster.controller.ClientController;
import ec.edu.monster.controller.LoginController;
import ec.edu.monster.controller.MainMenuController;
import ec.edu.monster.controller.TransactionController;
import ec.edu.monster.model.client.AccountClient;
import ec.edu.monster.model.client.ApiClient;
import ec.edu.monster.model.client.AuthClient;
import ec.edu.monster.model.client.ClientClient;
import ec.edu.monster.model.client.TransactionClient;
import ec.edu.monster.model.entity.UserSession;
import ec.edu.monster.model.service.AccountService;
import ec.edu.monster.model.service.AuthService;
import ec.edu.monster.model.service.ClientService;
import ec.edu.monster.model.service.TransactionService;
import ec.edu.monster.view.AccountView;
import ec.edu.monster.view.ClientView;
import ec.edu.monster.view.LoginView;
import ec.edu.monster.view.MainMenuView;
import ec.edu.monster.view.TransactionView;

public class App {

    public static void main(String[] args) {
        AuthService authService = ApiClient.getInstance().getApi().create(AuthService.class);
        LoginController loginController = new LoginController(new LoginView(), new AuthClient(authService));

        loginController.run();

        if (!UserSession.getInstance().isLoggedIn()) {
            return;
        }

        ClientService clientService = ApiClient.getInstance().getApi().create(ClientService.class);
        ClientClient clientClient = new ClientClient(clientService);
        ClientView clientView = new ClientView();
        ClientController clientController = new ClientController(clientView, clientClient);

        AccountService accountService = ApiClient.getInstance().getApi().create(AccountService.class);
        AccountClient accountClient = new AccountClient(accountService);
        AccountView accountView = new AccountView();
        AccountController accountController = new AccountController(accountView, accountClient);

        TransactionService transactionService = ApiClient.getInstance().getApi().create(TransactionService.class);
        TransactionClient transactionClient = new TransactionClient(transactionService);
        TransactionView transactionView = new TransactionView();
        TransactionController transactionController = new TransactionController(transactionView, transactionClient);

        MainMenuView menuView = new MainMenuView();
        MainMenuController mainMenuController = new MainMenuController(
                menuView,
                clientController,
                accountController,
                transactionController);

        mainMenuController.run();

        ApiClient.getInstance().shutdown();

        System.exit(0);
    }
}
