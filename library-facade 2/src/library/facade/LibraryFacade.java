package library.facade;

import library.subsystem.Book;
import library.subsystem.BookNotFoundException;
import library.subsystem.CatalogService;
import library.subsystem.FineCalculator;
import library.subsystem.InventoryService;
import library.subsystem.Loan;
import library.subsystem.LoanNotFoundException;
import library.subsystem.NoCopiesAvailableException;
import library.subsystem.NotificationFailedException;
import library.subsystem.NotificationService;
import library.subsystem.Reader;
import library.subsystem.ReaderAccountService;
import library.subsystem.ReaderNotFoundException;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;

public class LibraryFacade {
    private static final System.Logger LOG = System.getLogger(LibraryFacade.class.getName());

    private final CatalogService catalog;
    private final ReaderAccountService accounts;
    private final InventoryService inventory;
    private final FineCalculator fines;
    private final NotificationService notifications;
    private final Clock clock;

    public LibraryFacade(CatalogService catalog,
                         ReaderAccountService accounts,
                         InventoryService inventory,
                         FineCalculator fines,
                         NotificationService notifications,
                         Clock clock) {
        this.catalog = catalog;
        this.accounts = accounts;
        this.inventory = inventory;
        this.fines = fines;
        this.notifications = notifications;
        this.clock = clock;
    }

    public BorrowReceipt borrowBook(String readerId, String isbn) {
        try {
            return coordinateBorrowing(readerId, isbn);
        } catch (ReaderNotFoundException e) {
            throw new LibraryOperationException(FailureReason.READER_UNKNOWN, e.getMessage(), e);
        } catch (BookNotFoundException e) {
            throw new LibraryOperationException(FailureReason.BOOK_UNKNOWN, e.getMessage(), e);
        } catch (NoCopiesAvailableException e) {
            throw new LibraryOperationException(FailureReason.NO_COPIES_AVAILABLE, e.getMessage(), e);
        }
    }

    public ReturnReceipt returnBook(String readerId, String isbn) {
        try {
            return coordinateReturn(readerId, isbn);
        } catch (ReaderNotFoundException e) {
            throw new LibraryOperationException(FailureReason.READER_UNKNOWN, e.getMessage(), e);
        } catch (BookNotFoundException e) {
            throw new LibraryOperationException(FailureReason.BOOK_UNKNOWN, e.getMessage(), e);
        } catch (LoanNotFoundException e) {
            throw new LibraryOperationException(FailureReason.NOT_BORROWED, e.getMessage(), e);
        }
    }

    public BigDecimal payOutstandingFines(String readerId) {
        try {
            accounts.findReader(readerId);
            return fines.settleAll(readerId);
        } catch (ReaderNotFoundException e) {
            throw new LibraryOperationException(FailureReason.READER_UNKNOWN, e.getMessage(), e);
        }
    }

    private BorrowReceipt coordinateBorrowing(String readerId, String isbn) {
        Reader reader = accounts.findReader(readerId);
        ensureReaderMayBorrow(reader);
        Book book = catalog.findByIsbn(isbn);

        inventory.reserveCopy(isbn);
        Loan loan = openLoanOrReleaseCopy(readerId, isbn);

        notifyLoan(reader, book, loan);
        return new BorrowReceipt(reader.name(), book.title(), loan.dueOn());
    }

    private ReturnReceipt coordinateReturn(String readerId, String isbn) {
        Reader reader = accounts.findReader(readerId);
        Book book = catalog.findByIsbn(isbn);

        Loan loan = accounts.closeLoan(readerId, isbn);
        inventory.releaseCopy(isbn);
        BigDecimal fine = fines.chargeForLateReturn(readerId, loan.dueOn(), LocalDate.now(clock));

        notifyReturn(reader, book, fine);
        return new ReturnReceipt(book.title(), fine);
    }

    private void ensureReaderMayBorrow(Reader reader) {
        if (!reader.active()) {
            throw new LibraryOperationException(FailureReason.READER_BLOCKED,
                    "Reader account is blocked");
        }
        if (fines.outstandingFine(reader.id()).signum() > 0) {
            throw new LibraryOperationException(FailureReason.UNPAID_FINES,
                    "Reader has unpaid fines");
        }
        if (accounts.hasReachedLoanLimit(reader.id())) {
            throw new LibraryOperationException(FailureReason.LOAN_LIMIT_REACHED,
                    "Reader already has the maximum number of loans");
        }
    }

    private Loan openLoanOrReleaseCopy(String readerId, String isbn) {
        try {
            return accounts.openLoan(readerId, isbn, LocalDate.now(clock));
        } catch (RuntimeException e) {
            inventory.releaseCopy(isbn);
            throw new LibraryOperationException(FailureReason.UNEXPECTED_FAILURE,
                    "Could not record the loan", e);
        }
    }

    private void notifyLoan(Reader reader, Book book, Loan loan) {
        try {
            notifications.sendLoanConfirmation(reader, book, loan.dueOn());
        } catch (NotificationFailedException e) {
            LOG.log(System.Logger.Level.WARNING, "Loan confirmation not delivered: " + e.getMessage());
        }
    }

    private void notifyReturn(Reader reader, Book book, BigDecimal fine) {
        try {
            notifications.sendReturnConfirmation(reader, book, fine);
        } catch (NotificationFailedException e) {
            LOG.log(System.Logger.Level.WARNING, "Return confirmation not delivered: " + e.getMessage());
        }
    }
}
