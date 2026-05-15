package ec.edu.monster.model;

/**
 * Subtypes of a TRANSFER transaction.
 *
 * CREDIT – The source account holder pushes money to the target (classic wire transfer).
 *          The payer initiates the operation.
 *
 * DEBIT  – The target account holder pulls money from the source (direct debit).
 *          The payee initiates the operation and the source authorizes the charge.
 */
public enum TransferType {
    CREDIT,
    DEBIT
}
