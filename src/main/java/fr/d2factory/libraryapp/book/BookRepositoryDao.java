package fr.d2factory.libraryapp.book;

import java.time.LocalDate;
import java.util.List;

public interface BookRepositoryDao {
	/**
	 * This method allows to add a list of book.
	 * 
	 * @author mouna.chabaane
	 * @param books
	 */
	void addBooks(List<Book> books);

	/**
	 * this method allows to delete a book.
	 * 
	 * @author mouna.chabaane
	 * @param book
	 */

	void deleteBook(Book book);

	/**
	 * Remove a book from borrowed list when the member return his borrowed book.
	 * 
	 * @author mouna.chabaane
	 * @param book
	 * @param borrowedAt
	 */

	void removeBorrowedBook(Book book, LocalDate borrowedAt);

	/**
	 * find a book by isbn code.
	 * 
	 * @author mouna.chabaane
	 * @param isbnCode
	 * @return Book book
	 */
	Book findBook(long isbnCode);

	/**
	 * add a book in the borrowed list when a member borrow it.
	 * 
	 * @param book
	 * @param borrowedAt
	 */
	void saveBookBorrow(Book book, LocalDate borrowedAt);

	/**
	 * returns a book borrowed date if his already borrowed.
	 * 
	 * @author mouna.chabaane
	 * @param book
	 * @return LocaleDate borrowedAt
	 */
	LocalDate findBorrowedBookDate(Book book);

	/**
	 * add new book in available book list.
	 * 
	 * @author mouna.chabaane
	 * @param book
	 */
	void addBook(Book book);
}
