package ec.edu.monster.view;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import ec.edu.monster.model.dto.ClientRequest;
import ec.edu.monster.model.entity.Client;
import ec.edu.monster.model.enums.ClientStatus;

public class ClientView {
    private final Scanner scanner = new Scanner(System.in);

    public int showSubMenu() {
        System.out.println("\n--- GESTIÓN DE CLIENTES ---");
        System.out.println("1. Listar todos los clientes");
        System.out.println("2. Buscar cliente por ID");
        System.out.println("3. Buscar cliente por DNI");
        System.out.println("4. Registrar nuevo cliente");
        System.out.println("5. Actualizar cliente");
        System.out.println("6. Eliminar cliente");
        System.out.println("0. Volver al Menú Principal");
        return readOption(0, 6);
    }

    public ClientStatus askClientStatus() {
        System.out.println("--- Estado ---");
        System.out.println("1. Activo");
        System.out.println("2. Inactivo");

        while (true) {
            try {
                System.out.print("Seleccione una opción: ");
                String optionInput = scanner.nextLine();

                if (optionInput.isBlank())
                    return null;

                int option = Integer.parseInt(optionInput);

                switch (option) {
                    case 1:
                        return ClientStatus.ACTIVE;

                    case 2:
                        return ClientStatus.INACTIVE;

                    default:
                        System.out.println("Error: Opción fuera de rango. Intente nuevamente.");
                        break;
                }

            } catch (NumberFormatException e) {
                System.out.println("Error: Por favor, ingrese un número válido.");
                scanner.nextLine();
            }
        }
    }

    public ClientRequest askClientData(Client currentData) {
        System.out.println(currentData == null ? "\n== REGISTRAR CLIENTE ==" : "\n== ACTUALIZAR CLIENTE ==");

        System.out.print("Nombre" + (currentData != null ? " [" + currentData.getName() + "]: " : ": "));
        String name = scanner.nextLine();
        if (name.isBlank() && currentData != null)
            name = currentData.getName();

        System.out.print("DNI" + (currentData != null ? " [" + currentData.getDni() + "]: " : ": "));
        String dni = scanner.nextLine();
        if (dni.isBlank() && currentData != null)
            dni = currentData.getDni();

        System.out.print("Email" + (currentData != null ? " [" + currentData.getEmail() + "]: " : ": "));
        String email = scanner.nextLine();
        if (email.isBlank() && currentData != null)
            email = currentData.getEmail();

        System.out.print("Teléfono" + (currentData != null ? " [" + currentData.getPhone() + "]: " : ": "));
        String phone = scanner.nextLine();
        if (phone.isBlank() && currentData != null)
            phone = currentData.getPhone();

        System.out.println("Status" + (currentData != null ? " [" + currentData.getStatus().getLabel() + "]: "
                : " [" + ClientStatus.ACTIVE.getLabel() + "]: "));
        ClientStatus status = askClientStatus();
        if (status == null) {
            if (currentData != null)
                status = currentData.getStatus();
            else
                status = ClientStatus.ACTIVE;
        }

        System.out.print("Usuario" + (currentData != null ? " [" + currentData.getUsername() + "]: " : ": "));
        String username = scanner.nextLine();
        if (username.isBlank() && currentData != null)
            username = currentData.getUsername();

        String password = null;
        if (currentData == null) {
            System.out.print("Contraseña: ");
            password = scanner.nextLine();
        }

        return new ClientRequest(name, dni, email, phone, status, username, password);
    }

    public Long askId(String action) {
        System.out.print("Ingrese el ID del cliente a " + action + ": ");
        try {
            Long id = scanner.nextLong();
            scanner.nextLine();
            return id;
        } catch (InputMismatchException e) {
            scanner.nextLine();
            System.out.println("Error: ID inválido.");
            return null;
        }
    }

    public String askDni() {
        System.out.print("Ingrese el DNI del cliente a buscar: ");
        return scanner.nextLine();
    }

    public void showClients(List<Client> clients) {
        if (clients.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }
        System.out.println(
                "\n===================================== LISTADO DE CLIENTES ===============================================");
        System.out.printf("%-10s %-20s %-15s %-25s %-12s %-10s %-10s %-20s\n",
                "Id", "Nombre", "DNI",
                "Correo electrónico", "Teléfono", "Estado",
                "Usuario Id", "Usuario");
        System.out.println(
                "---------------------------------------------------------------------------------------------------------");
        for (Client c : clients) {
            System.out.printf("%-10s %-20s %-15s %-15s %-10s %-8s %-10s %-20s\n",
                    c.getId(), c.getName(), c.getDni(),
                    c.getEmail(), c.getPhone(), c.getStatus().getLabel(),
                    c.getUserId(), c.getUsername());
        }
    }

    public void showClientDetails(Client c) {
        System.out.println("\n=== DETALLE DEL CLIENTE ===");
        System.out.println("Id: " + c.getId());
        System.out.println("Nombre: " + c.getName());
        System.out.println("DNI: " + c.getDni());
        System.out.println("Correo electrónico: " + c.getEmail());
        System.out.println("Teléfono: " + c.getPhone());
        System.out.println("Estado: " + c.getStatus().getLabel());
        System.out.println("Usuario Id: " + c.getUserId());
        System.out.println("Usuario: " + c.getUsername());
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
}