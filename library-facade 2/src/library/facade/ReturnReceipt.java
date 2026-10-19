package library.facade;

import java.math.BigDecimal;

public record ReturnReceipt(String bookTitle, BigDecimal fine) {
    public boolean wasLate() {
        return fine.signum() > 0;
    }
}
