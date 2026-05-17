package ec.edu.monster.view;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import ec.edu.monster.model.dto.AccountRequest;
import ec.edu.monster.model.dto.AccountStatusRequest;
import ec.edu.monster.model.entity.Account;
import ec.edu.monster.model.enums.AccountStatus;
import ec.edu.monster.model.enums.AccountType;

public class AccountView {
    private final Scanner scanner = new Scanner(System.in);
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

    public int showSubMenu() {
        System.out.println("\n--- GESTIÓN DE CUENTAS ---");
        System.out.println("1. Listar todas las cuentas");
        System.out.println("2. Buscar cuenta por ID");
        System.out.println("3. Consultar saldo de una cuenta");
        System.out.println("4. Listar cuentas de un cliente");
        System.out.println("5. Abrir nueva cuenta");
        System.out.println("6. Cambiar estado de cuenta (Activar/Bloquear)");
        System.out.println("0. Volver al Menú Principal");
        return readOption(0, 6);
    }

    public AccountRequest askAccountData() {
        System.out.println("\n== APERTURA DE CUENTA ==");

        System.out.print("Ingrese el ID del Cliente titular: ");
        Long clientId = readLong();

        System.out.println("Seleccione Tipo de Cuenta:");
        System.out.println("1. AHORROS");
        System.out.println("2. CORRIENTE");
        int typeOpt = readOption(1, 2);
        AccountType type = (typeOpt == 1) ? AccountType.SAVINGS : AccountType.CURRENT;

        return new AccountRequest(clientId, type, AccountStatus.ACTIVE);
    }

    public AccountStatusRequest askStatusUpdate(AccountStatus currentStatus) {
        System.out.println("\n== CAMBIAR ESTADO DE CUENTA ==");
        System.out.println("Estado actual: " + currentStatus);
        System.out.println("Seleccione el nuevo estado:");
        System.out.println("1. ACTIVA");
        System.out.println("2. BLOQUEADA");

        int opt = readOption(1, 2);
        AccountStatus newStatus = (opt == 1) ? AccountStatus.ACTIVE : AccountStatus.BLOCKED;
        return new AccountStatusRequest(newStatus);
    }

    public Long askAccountId(String action) {
        System.out.print("Ingrese el ID de la cuenta para " + action + ": ");
        return readLong();
    }

    public Long askClientId() {
        System.out.print("Ingrese el ID del cliente para listar sus cuentas: ");
        return readLong();
    }

    public void showAccounts(List<Account> accounts) {
        if (accounts.isEmpty()) {
            System.out.println("No se encontraron cuentas.");
            return;
        }
        System.out
                .println("\n================================== LISTADO DE CUENTAS ==================================");
        System.out.printf("%-10s %-20s %-15s %-12s %-12s %-10s\n", "Id", "Número cuenta", "Saldo", "Tipo", "Estado",
                "Cliente Id");
        System.out.println("----------------------------------------------------------------------------------------");
        for (Account a : accounts) {
            System.out.printf("%-10d %-20s %-15s %-12s %-12s %-10d\n",
                    a.getId(),
                    a.getAccountNumber(),
                    currencyFormatter.format(a.getBalance()),
                    a.getType().getLabel(),
                    a.getStatus().getLabel(),
                    a.getClientId());
        }
    }

    public void showAccountDetails(Account a) {
        System.out.println("\n=== DETALLE DE LA CUENTA ===");
        System.out.println("ID: " + a.getId());
        System.out.println("Número de Cuenta: " + a.getAccountNumber());
        System.out.println("Saldo Disponible: " + currencyFormatter.format(a.getBalance()));
        System.out.println("Tipo: " + a.getType());
        System.out.println("Estado: " + a.getStatus());
        System.out.println("ID Titular (Cliente): " + a.getClientId());
    }

    public void showBalance(BigDecimal balance) {
        System.out.println("\n=================================");
        System.out.println(" SALDO ACTUAL: " + currencyFormatter.format(balance));
        System.out.println("=================================");
    }

    public void showSuccess(String message) {
        System.out.println("✔ Éxito: " + message);
    }

    public void showError(String message) {
        System.err.println("✘ Error: " + message);
    }

    private int readOption(int min, int max) {
        while (true) {
            try {
                System.out.print("Seleccione una opción: ");
                int option = scanner.nextInt();
                scanner.nextLine();
                if (option >= min && option <= max)
                    return option;
                System.out.println("Opción fuera de rango.");
            } catch (InputMismatchException e) {
                System.out.println("Ingrese un número válido.");
                scanner.nextLine();
            }
        }
    }

    private Long readLong() {
        while (true) {
            try {
                Long val = scanner.nextLong();
                scanner.nextLine();
                return val;
            } catch (InputMismatchException e) {
                System.out.println("Error: Debe ingresar un ID numérico válido.");
                scanner.nextLine();
            }
        }
    }
}
