package ec.edu.monster.exception;

public class TransactionException extends RuntimeException {

    private final int status;

    public TransactionException(String message, int status) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
