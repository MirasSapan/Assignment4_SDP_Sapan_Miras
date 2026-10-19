package library.facade;

import java.time.LocalDate;

public record BorrowReceipt(String readerName, String bookTitle, LocalDate dueOn) {
}
