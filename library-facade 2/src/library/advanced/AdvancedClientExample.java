package library.advanced;

import library.subsystem.Book;
import library.subsystem.CatalogService;
import library.subsystem.InventoryService;

public class AdvancedClientExample {
    public static void main(String[] args) {
        CatalogService catalog = new CatalogService();
        InventoryService inventory = new InventoryService();

        catalog.addBook(new Book("978-0-13-235088-4", "Clean Code", "Robert C. Martin"));
        inventory.registerCopies("978-0-13-235088-4", 5);

        System.out.println(catalog.findByIsbn("978-0-13-235088-4").title()
                + ": " + inventory.availableCopies("978-0-13-235088-4") + " copies on the shelf");
    }
}
