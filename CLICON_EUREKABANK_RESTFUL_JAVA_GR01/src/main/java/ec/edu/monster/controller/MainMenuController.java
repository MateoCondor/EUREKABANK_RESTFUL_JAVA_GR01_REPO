package ec.edu.monster.controller;

import ec.edu.monster.model.entity.UserSession;
import ec.edu.monster.view.MainMenuView;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MainMenuController {
    private final MainMenuView view;

    private final ClientController clientController;
    private final AccountController accountController;
    private final TransactionController transactionController;

    public void run() {
        UserSession session = UserSession.getInstance();
        boolean running = true;

        while (running) {
            int option = view.showMenu(session.getUsername(), session.getRole());

            switch (option) {
                case 1 -> clientController.run();
                case 2 -> accountController.run();
                case 3 -> transactionController.run();
                case 0 -> {
                    view.showGoodbye();
                    session.clear();
                    running = false;
                }
            }
        }
    }
}