package library.subsystem;

public class LoanNotFoundException extends SubsystemException {
    public LoanNotFoundException(String details) {
        super(details);
    }
}
