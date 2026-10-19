package library.subsystem;

import java.time.LocalDate;

public record Loan(String readerId, String isbn, LocalDate borrowedOn, LocalDate dueOn) {
}
