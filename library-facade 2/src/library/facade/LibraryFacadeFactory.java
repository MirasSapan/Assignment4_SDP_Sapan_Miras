package library.facade;

import library.subsystem.Book;
import library.subsystem.CatalogService;
import library.subsystem.FineCalculator;
import library.subsystem.InventoryService;
import library.subsystem.NotificationService;
import library.subsystem.Reader;
import library.subsystem.ReaderAccountService;

import java.time.Clock;

public final class LibraryFacadeFactory {
    private LibraryFacadeFactory() {
    }

    public static LibraryFacade createWithSampleData(Clock clock) {
        CatalogService catalog = new CatalogService();
        InventoryService inventory = new InventoryService();
        ReaderAccountService accounts = new ReaderAccountService();

        addBook(catalog, inventory, new Book("978-0-13-468599-1", "Effective Java", "Joshua Bloch"), 2);
        addBook(catalog, inventory, new Book("978-0-13-235088-4", "Clean Code", "Robert C. Martin"), 1);
        addBook(catalog, inventory, new Book("978-0-201-63361-0", "Design Patterns", "Gamma, Helm, Johnson, Vlissides"), 1);
        addBook(catalog, inventory, new Book("978-0-596-00712-6", "Head First Design Patterns", "Freeman, Robson"), 1);

        accounts.registerReader(new Reader("R001", "Aigerim", true));
        accounts.registerReader(new Reader("R002", "Daniyar", true));
        accounts.registerReader(new Reader("R003", "Aruzhan", false));

        return new LibraryFacade(catalog, accounts, inventory,
                new FineCalculator(), new NotificationService(), clock);
    }

    private static void addBook(CatalogService catalog, InventoryService inventory, Book book, int copies) {
        catalog.addBook(book);
        inventory.registerCopies(book.isbn(), copies);
    }
}
