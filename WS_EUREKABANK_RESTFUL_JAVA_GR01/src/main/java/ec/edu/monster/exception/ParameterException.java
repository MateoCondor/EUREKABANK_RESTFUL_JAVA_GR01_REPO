package ec.edu.monster.exception;

public class ParameterException extends RuntimeException {

    private final int status;

    public ParameterException(String message, int status) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
