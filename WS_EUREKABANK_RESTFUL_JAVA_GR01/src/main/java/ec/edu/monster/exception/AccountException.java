package ec.edu.monster.exception;

public class AccountException extends RuntimeException {

    private final int status;

    public AccountException(String message, int status) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
