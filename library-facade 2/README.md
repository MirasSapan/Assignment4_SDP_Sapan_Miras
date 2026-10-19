# Library Facade (Assignment #4, Facade pattern)

Borrowing and returning a library book looks simple to a client, but it needs
five collaborating subsystem classes in the right order. `LibraryFacade`
hides that orchestration behind three methods.

## Run (JDK 17+)

```bash
mkdir out
javac -d out $(find src -name '*.java')       # Windows: javac -d out src\library\*\*.java
java -cp out library.client.Main               # client that uses ONLY the Facade
java -cp out library.advanced.AdvancedClientExample   # bypasses the Facade on purpose
```

## Structure

```
library.subsystem   CatalogService, ReaderAccountService, InventoryService,
                    FineCalculator, NotificationService (+ Book, Reader, Loan, exceptions)
library.facade      LibraryFacade (+ LibraryFacadeFactory, receipts, FailureReason,
                    LibraryOperationException)
library.client      Main, AdjustableClock   (uses only library.facade)
library.advanced    AdvancedClientExample   (uses subsystem classes directly)
```

### Facade API

| Method | Hides |
|---|---|
| `borrowBook(readerId, isbn)` | reader check, fine check, loan limit, catalog lookup, copy reservation, loan creation, notification |
| `returnBook(readerId, isbn)` | loan closing, copy release, fine calculation, notification |
| `payOutstandingFines(readerId)` | reader check, fine settlement |

### Pattern checklist

- 5 subsystem classes with real responsibilities (>= 4 required).
- Subsystem classes have no reference to `LibraryFacade`, and `AdvancedClientExample` uses them without it.
- `Main` imports only `library.facade` (the factory wires the subsystem and loads sample data).
- Optional second facade: not implemented.

## Clean Code principles used

1. **Meaningful names.** `borrowBook`, `returnBook`, `payOutstandingFines`, `ensureReaderMayBorrow`,
   `openLoanOrReleaseCopy`: the API reads like the business process.
2. **Single Responsibility.** Each subsystem class has one job (catalog, inventory, accounts, fines,
   messages). The Facade only coordinates: loan limit, fine amount and due date are computed in
   the subsystem, never in the Facade.
3. **No leaking of subsystem details.** The public API takes and returns only `String`, receipts
   (`BorrowReceipt`, `ReturnReceipt`) and `BigDecimal`. `Book`, `Reader`, `Loan` and all subsystem
   exceptions never cross the Facade boundary.
4. **Proper error handling.** Subsystem exceptions are translated into one
   `LibraryOperationException` with a `FailureReason` enum, so the client handles a single type.
   If recording the loan fails after a copy was reserved, the copy is released (compensating action).
   A failed notification is logged and does not undo a successful loan.
5. **Small methods, one level of abstraction.** Public methods only catch and translate;
   `coordinateBorrowing` / `coordinateReturn` read as a list of steps; checks and rollback are separate methods.
6. **Dependency Injection.** The Facade receives its collaborators and a `Clock` through the constructor,
   which makes it testable and lets the demo move time forward to show a late fine.
7. **Immutability and value objects.** `Book`, `Reader`, `Loan` and the receipts are records;
   money uses `BigDecimal`, not `double`.
8. **No magic numbers.** `MAX_ACTIVE_LOANS`, `LOAN_PERIOD_DAYS`, `DAILY_RATE` are named constants.

## Defense cheat sheet

- **Why Facade and not Adapter/Mediator?** The goal is to simplify a complex subsystem for clients. Adapter changes an
  interface, Mediator makes colleagues talk through it (here subsystem classes do not know the Facade at all).
- **Does the Facade stop clients from using the subsystem?** No. Advanced clients may bypass it
  (`AdvancedClientExample`).
- **Where would you add a feature, such as a book reservation queue?** A new subsystem class plus one
  coordination step in the Facade; clients do not change.
- **Is the Facade a God object?** It must not be: it holds no business rules, only the order of calls.
  If it grows, split it into several facades.
- **Weak spot to admit:** the order of steps in `returnBook` is not transactional;
  a real system would use a database transaction.
