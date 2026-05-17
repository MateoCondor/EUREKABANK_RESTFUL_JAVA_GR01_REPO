package ec.edu.monster.view;

import java.util.InputMismatchException;
import java.util.Scanner;

import ec.edu.monster.model.enums.UserRole;

public class MainMenuView {
    private final Scanner scanner = new Scanner(System.in);

    public int showMenu(String username, UserRole role) {
        System.out.println("\n========================================");
        System.out.println("   EUREKABANK - SISTEMA DE GESTIÓN");
        System.out.println("========================================");
        System.out.println("Usuario: " + username + " | Rol: " + role.getLabel());
        System.out.println("----------------------------------------");
        System.out.println("1. Gestión de Clientes");
        System.out.println("2. Gestión de Cuentas");
        System.out.println("3. Gestión de Transacciones");
        System.out.println("0. Cerrar Sesión y Salir");
        System.out.println("----------------------------------------");

        return readOption(0, 3);
    }

    private int readOption(int min, int max) {
        while (true) {
            try {
                System.out.print("Seleccione una opción: ");
                int option = scanner.nextInt();
                scanner.nextLine();

                if (option >= min && option <= max) {
                    return option;
                }
                System.out.println("Error: Opción fuera de rango. Intente nuevamente.");
            } catch (InputMismatchException e) {
                System.out.println("Error: Por favor, ingrese un número válido.");
                scanner.nextLine();
            }
        }
    }

    public void showGoodbye() {
        System.out.println("\nCerrando sesión... Gracias por usar EurekaBank.");
    }
}