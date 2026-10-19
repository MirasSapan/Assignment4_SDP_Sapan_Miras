package library.client;

import library.facade.BorrowReceipt;
import library.facade.LibraryFacade;
import library.facade.LibraryFacadeFactory;
import library.facade.LibraryOperationException;
import library.facade.ReturnReceipt;

import java.time.Instant;

public class Main {
    private static final String EFFECTIVE_JAVA = "978-0-13-468599-1";
    private static final String CLEAN_CODE = "978-0-13-235088-4";
    private static final String DESIGN_PATTERNS = "978-0-201-63361-0";
    private static final String HEAD_FIRST = "978-0-596-00712-6";

    public static void main(String[] args) {
        AdjustableClock clock = new AdjustableClock(Instant.parse("2026-10-01T09:00:00Z"));
        LibraryFacade library = LibraryFacadeFactory.createWithSampleData(clock);

        section("1. Successful borrowing");
        borrow(library, "R001", EFFECTIVE_JAVA);

        section("2. Refusals: no free copy, blocked reader, unknown book, unknown reader");
        borrow(library, "R002", CLEAN_CODE);
        borrow(library, "R001", CLEAN_CODE);
        borrow(library, "R003", EFFECTIVE_JAVA);
        borrow(library, "R001", "000-0-00-000000-0");
        borrow(library, "R999", EFFECTIVE_JAVA);

        section("3. Loan limit (max 3 active loans)");
        borrow(library, "R002", DESIGN_PATTERNS);
        borrow(library, "R002", HEAD_FIRST);
        borrow(library, "R002", EFFECTIVE_JAVA);

        section("4. Late return creates a fine, fine blocks borrowing until paid");
        clock.advanceDays(20);
        giveBack(library, "R001", EFFECTIVE_JAVA);
        borrow(library, "R001", EFFECTIVE_JAVA);
        System.out.println("   paid: " + library.payOutstandingFines("R001"));
        borrow(library, "R001", EFFECTIVE_JAVA);

        section("5. Returning a book that was never borrowed");
        giveBack(library, "R001", HEAD_FIRST);
    }

    private static void borrow(LibraryFacade library, String readerId, String isbn) {
        try {
            BorrowReceipt receipt = library.borrowBook(readerId, isbn);
            System.out.println("   OK: " + receipt.readerName() + " -> \"" + receipt.bookTitle()
                    + "\", due " + receipt.dueOn());
        } catch (LibraryOperationException e) {
            System.out.println("   REFUSED (" + e.reason() + "): " + e.getMessage());
        }
    }

    private static void giveBack(LibraryFacade library, String readerId, String isbn) {
        try {
            ReturnReceipt receipt = library.returnBook(readerId, isbn);
            System.out.println("   OK: returned \"" + receipt.bookTitle() + "\", late=" + receipt.wasLate()
                    + ", fine=" + receipt.fine());
        } catch (LibraryOperationException e) {
            System.out.println("   REFUSED (" + e.reason() + "): " + e.getMessage());
        }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("== " + title);
    }
}
