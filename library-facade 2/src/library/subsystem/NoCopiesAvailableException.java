package library.subsystem;

public class NoCopiesAvailableException extends SubsystemException {
    public NoCopiesAvailableException(String details) {
        super(details);
    }
}
