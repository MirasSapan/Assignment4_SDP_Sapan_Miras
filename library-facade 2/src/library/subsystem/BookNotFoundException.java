package library.subsystem;

public class BookNotFoundException extends SubsystemException {
    public BookNotFoundException(String details) {
        super(details);
    }
}
