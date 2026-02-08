package vendingmachine;

public class InvalidTransactionException extends Throwable {
    public InvalidTransactionException(String message) {
        super(message);
    }
}
