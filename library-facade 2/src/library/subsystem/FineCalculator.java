package library.subsystem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class FineCalculator {
    public static final BigDecimal DAILY_RATE = new BigDecimal("0.50");

    private final Map<String, BigDecimal> unpaidFines = new HashMap<>();

    public BigDecimal chargeForLateReturn(String readerId, LocalDate dueOn, LocalDate returnedOn) {
        long daysLate = Math.max(0, ChronoUnit.DAYS.between(dueOn, returnedOn));
        BigDecimal fine = DAILY_RATE.multiply(BigDecimal.valueOf(daysLate));
        if (fine.signum() > 0) {
            unpaidFines.merge(readerId, fine, BigDecimal::add);
        }
        return fine;
    }

    public BigDecimal outstandingFine(String readerId) {
        return unpaidFines.getOrDefault(readerId, BigDecimal.ZERO);
    }

    public BigDecimal settleAll(String readerId) {
        BigDecimal paid = outstandingFine(readerId);
        unpaidFines.remove(readerId);
        return paid;
    }
}
