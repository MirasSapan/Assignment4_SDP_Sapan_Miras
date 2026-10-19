package library.subsystem;

import java.math.BigDecimal;
import java.time.LocalDate;

public class NotificationService {
    public void sendLoanConfirmation(Reader reader, Book book, LocalDate dueOn) {
        send(reader, "You borrowed \"" + book.title() + "\". Please return it by " + dueOn + ".");
    }

    public void sendReturnConfirmation(Reader reader, Book book, BigDecimal fine) {
        String suffix = fine.signum() > 0 ? " Late fine: " + fine + "." : "";
        send(reader, "Thanks for returning \"" + book.title() + "\"." + suffix);
    }

    private void send(Reader reader, String text) {
        System.out.println("   [message to " + reader.name() + "] " + text);
    }
}
