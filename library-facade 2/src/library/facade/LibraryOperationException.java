package library.facade;

public class LibraryOperationException extends RuntimeException {
    private final FailureReason reason;

    public LibraryOperationException(FailureReason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public LibraryOperationException(FailureReason reason, String message, Throwable cause) {
        super(message, cause);
        this.reason = reason;
    }

    public FailureReason reason() {
        return reason;
    }
}
