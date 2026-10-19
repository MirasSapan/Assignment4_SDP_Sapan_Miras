package library.subsystem;

import java.util.HashMap;
import java.util.Map;

public class InventoryService {
    private final Map<String, Integer> availableCopies = new HashMap<>();

    public void registerCopies(String isbn, int copies) {
        availableCopies.merge(isbn, copies, Integer::sum);
    }

    public int availableCopies(String isbn) {
        return availableCopies.getOrDefault(isbn, 0);
    }

    public void reserveCopy(String isbn) {
        int available = availableCopies(isbn);
        if (available == 0) {
            throw new NoCopiesAvailableException("All copies of " + isbn + " are on loan");
        }
        availableCopies.put(isbn, available - 1);
    }

    public void releaseCopy(String isbn) {
        availableCopies.merge(isbn, 1, Integer::sum);
    }
}
