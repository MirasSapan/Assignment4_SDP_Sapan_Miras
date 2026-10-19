package library.subsystem;

public abstract class SubsystemException extends RuntimeException {
    protected SubsystemException(String message) {
        super(message);
    }
}
