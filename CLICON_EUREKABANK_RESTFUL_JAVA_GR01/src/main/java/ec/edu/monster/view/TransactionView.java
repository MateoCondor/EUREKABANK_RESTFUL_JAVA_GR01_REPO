package ec.edu.monster.view;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import ec.edu.monster.model.dto.DepositRequest;
import ec.edu.monster.model.dto.TransferRequest;
import ec.edu.monster.model.dto.WithdrawRequest;
import ec.edu.monster.model.entity.Transaction;
import ec.edu.monster.model.enums.TransferType;

public class TransactionView {
    private final Scanner scanner = new Scanner(System.in);
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public int showSubMenu() {
        System.out.println("\n--- MÓDULO DE TRANSACCIONES ---");
        System.out.println("1. Ver historial de transacciones por Cuenta");
        System.out.println("2. Realizar un Depósito");
        System.out.println("3. Realizar un Retiro");
        System.out.println("4. Realizar una Transferencia");
        System.out.println("0. Volver al Menú Principal");
        return readOption(0, 4);
    }

    public DepositRequest askDepositData() {
        System.out.println("\n== REALIZAR DEPÓSITO ==");
        System.out.print("Id de la Cuenta de destino: ");
        Long accountId = readLong();
        System.out.print("Monto a depositar: ");
        BigDecimal amount = readBigDecimal();
        System.out.print("Descripción / Concepto: ");
        String desc = scanner.nextLine();
        return new DepositRequest(accountId, amount, desc);
    }

    public WithdrawRequest askWithdrawData() {
        System.out.println("\n== REALIZAR RETIRO ==");
        System.out.print("Id de la Cuenta: ");
        Long accountId = readLong();
        System.out.print("Monto a retirar: ");
        BigDecimal amount = readBigDecimal();
        System.out.print("Descripción: ");
        String desc = scanner.nextLine();
        return new WithdrawRequest(accountId, amount, desc);
    }

    public TransferType askTransferType() {
        System.out.println("Seleccione Tipo de transferencia:");
        System.out.println("1. Crédito");
        System.out.println("2. Débito");
        int typeOpt = readOption(1, 2);
        return typeOpt == 1 ? TransferType.CREDIT : TransferType.DEBIT;
    }

    public TransferRequest askTransferData() {
        System.out.println("\n== REALIZAR TRANSFERENCIA ==");
        System.out.print("Id de la Cuenta de ORIGEN: ");
        Long sourceId = readLong();
        System.out.print("Id de la Cuenta de DESTINO: ");
        Long targetId = readLong();
        System.out.print("Monto a transferir: ");
        BigDecimal amount = readBigDecimal();
        System.out.print("Descripción: ");
        String desc = scanner.nextLine();
        System.out.println("Tipo de transferencia");
        TransferType transferType = askTransferType();

        return new TransferRequest(sourceId, targetId, amount, desc, transferType);
    }

    public Long askAccountId() {
        System.out.print("Ingrese el Id de la cuenta para ver su historial: ");
        return readLong();
    }

    public void showHistory(List<Transaction> list) {
        if (list.isEmpty()) {
            System.out.println("No se registran transacciones para esta cuenta.");
            return;
        }
        System.out.println(
                "\n====================================== HISTORIAL DE TRANSACCIONES ======================================");
        System.out.printf("%-6s %-12s %-20s %-12s %-8s %-10s %-10s %-20s\n",
                "Id", "Tipo", "Fecha", "Monto", "Comisión", "Origen", "Destino", "Descripción");
        System.out.println(
                "--------------------------------------------------------------------------------------------------------");
        for (Transaction t : list) {
            String source = t.getSourceAccountId() != null ? String.valueOf(t.getSourceAccountId()) : "-";
            String target = t.getTargetAccountId() != null ? String.valueOf(t.getTargetAccountId()) : "-";

            System.out.printf("%-6d %-12s %-20s %-12s %-8s %-10s %-10s %-20s\n",
                    t.getId(),
                    t.getType().getLabel(),
                    t.getDate() != null ? t.getDate().format(dateFormatter) : "-",
                    currencyFormatter.format(t.getAmount()),
                    currencyFormatter.format(t.getFee() != null ? t.getFee() : BigDecimal.ZERO),
                    source,
                    target,
                    t.getDescription() != null ? t.getDescription() : "");
        }
    }

    public void showReceipt(Transaction t, String message) {
        System.out.println("\n✔ " + message);
        System.out.println("=========================================");
        System.out.println("        COMPROBANTE DE OPERACIÓN        ");
        System.out.println("=========================================");
        System.out.println("Transacción Id : " + t.getId());
        System.out.println("Tipo           : " + t.getType().getLabel());
        System.out.println("Monto          : " + currencyFormatter.format(t.getAmount()));
        System.out.println(
                "Comisión : " + currencyFormatter.format(t.getFee() != null ? t.getFee() : BigDecimal.ZERO));
        System.out.println("Fecha/Hora     : " + (t.getDate() != null ? t.getDate().format(dateFormatter) : "-"));
        if (t.getSourceAccountId() != null)
            System.out.println("Cuenta Origen  : " + t.getSourceAccountId());
        if (t.getTargetAccountId() != null)
            System.out.println("Cuenta Destino : " + t.getTargetAccountId());
        System.out.println("Detalle        : " + t.getDescription());
        System.out.println("=========================================");
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
                System.out.println("Error: Ingrese un identificador numérico válido.");
                scanner.nextLine();
            }
        }
    }

    private BigDecimal readBigDecimal() {
        while (true) {
            try {
                BigDecimal val = scanner.nextBigDecimal();
                scanner.nextLine();
                if (val.compareTo(BigDecimal.ZERO) > 0)
                    return val;
                System.out.println("El monto debe ser mayor a cero.");
            } catch (InputMismatchException e) {
                System.out.println("Error: Ingrese un monto decimal válido (use coma o punto según su localidad).");
                scanner.nextLine();
            }
        }
    }
}
