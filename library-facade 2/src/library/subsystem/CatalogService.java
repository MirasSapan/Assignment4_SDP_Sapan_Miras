package library.subsystem;

import java.util.HashMap;
import java.util.Map;

public class CatalogService {
    private final Map<String, Book> booksByIsbn = new HashMap<>();

    public void addBook(Book book) {
        booksByIsbn.put(book.isbn(), book);
    }

    public Book findByIsbn(String isbn) {
        Book book = booksByIsbn.get(isbn);
        if (book == null) {
            throw new BookNotFoundException("No book with ISBN " + isbn);
        }
        return book;
    }
}
