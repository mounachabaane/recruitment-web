package fr.d2factory.libraryapp.book;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The book repository emulates a database via 2 HashMaps
 */
public class BookRepository implements BookRepositoryDao {
	private Map<ISBN, Book> availableBooks = new HashMap<>();
	private Map<Book, LocalDate> borrowedBooks = new HashMap<>();

	public BookRepository() {
		super();

	}

	@Override
	public void addBooks(List<Book> books) {
		books.forEach(item -> availableBooks.put(item.getIsbn(), item));

	}

	@Override
	public void deleteBook(Book book) {

		if (availableBooks.containsKey(book.getIsbn())) {
			availableBooks.remove(book.getIsbn(), book);
		}

	}

	/**
	 * remove borrowed book from borrowedBooks when member return it.
	 * 
	 * @param book
	 */
	@Override
	public void removeBorrowedBook(Book book, LocalDate borrowedAt) {

		if (borrowedBooks.containsKey(book)) {
			borrowedBooks.remove(book, borrowedAt);
		}

	}

	
	@Override
	public Book findBook(long isbnCode) {
		Book book = new Book();

		book = availableBooks.entrySet().stream().filter(x -> x.getKey().isbnCode == isbnCode).map(x -> x.getValue())
				.findFirst().orElse(new Book());

		return book;
	}

	@Override
	public void saveBookBorrow(Book book, LocalDate borrowedAt) {
		if (book.getIsbn() != null && borrowedAt != null) {

			borrowedBooks.put(book, borrowedAt);

		}
	}

	@Override
	public LocalDate findBorrowedBookDate(Book book) {
		LocalDate localDate = null;

		localDate = borrowedBooks.entrySet().stream()
				.filter(x -> x.getKey().getIsbn().isbnCode == book.getIsbn().isbnCode).map(x -> x.getValue())
				.findFirst().orElse(null);

		return localDate;
	}

	public Map<ISBN, Book> getAvailableBooks() {
		return availableBooks;
	}

	public void setAvailableBooks(Map<ISBN, Book> availableBooks) {
		this.availableBooks = availableBooks;
	}

	public Map<Book, LocalDate> getBorrowedBooks() {
		return borrowedBooks;
	}

	public void setBorrowedBooks(Map<Book, LocalDate> borrowedBooks) {
		this.borrowedBooks = borrowedBooks;
	}

	@Override
	public void addBook(Book book) {
		availableBooks.put(book.getIsbn(), book);

	}

}
