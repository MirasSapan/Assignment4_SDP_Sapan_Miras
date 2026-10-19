package library.subsystem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReaderAccountService {
    public static final int MAX_ACTIVE_LOANS = 3;
    public static final int LOAN_PERIOD_DAYS = 14;

    private final Map<String, Reader> readersById = new HashMap<>();
    private final Map<String, List<Loan>> activeLoansByReader = new HashMap<>();

    public void registerReader(Reader reader) {
        readersById.put(reader.id(), reader);
    }

    public Reader findReader(String readerId) {
        Reader reader = readersById.get(readerId);
        if (reader == null) {
            throw new ReaderNotFoundException("No reader with id " + readerId);
        }
        return reader;
    }

    public boolean hasReachedLoanLimit(String readerId) {
        return loansOf(readerId).size() >= MAX_ACTIVE_LOANS;
    }

    public Loan openLoan(String readerId, String isbn, LocalDate borrowedOn) {
        findReader(readerId);
        Loan loan = new Loan(readerId, isbn, borrowedOn, borrowedOn.plusDays(LOAN_PERIOD_DAYS));
        activeLoansByReader.computeIfAbsent(readerId, id -> new ArrayList<>()).add(loan);
        return loan;
    }

    public Loan closeLoan(String readerId, String isbn) {
        List<Loan> loans = loansOf(readerId);
        for (Loan loan : loans) {
            if (loan.isbn().equals(isbn)) {
                loans.remove(loan);
                return loan;
            }
        }
        throw new LoanNotFoundException("Reader " + readerId + " has not borrowed " + isbn);
    }

    private List<Loan> loansOf(String readerId) {
        return activeLoansByReader.computeIfAbsent(readerId, id -> new ArrayList<>());
    }
}
