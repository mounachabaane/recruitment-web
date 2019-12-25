package fr.d2factory.libraryapp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.book.IBookRepositoryDao;
import fr.d2factory.libraryapp.book.ISBN;
import fr.d2factory.libraryapp.member.Member;
import fr.d2factory.libraryapp.member.Resident;
import fr.d2factory.libraryapp.utils.IdGenerator;

public class TownsvilleLibraryApp {

	private static final Logger LOGGER = LoggerFactory.getLogger(TownsvilleLibraryApp.class);
	static IdGenerator idGenerator = new IdGenerator();

	public static void main(String[] args) {

		String uuid = idGenerator.generateUniqueId();
		MDC.put("id", uuid);
		System.out.println("*** Please check the log file townsville-library.log for debugging ***");
		LOGGER.info("***Start testing from main****");

		IBookRepositoryDao bookRepository = new BookRepository();

		LOGGER.info("Testing with a resident member who has no late book");
		Member resident = new Resident("Jhon", 30, false);

		resident.setBookRepositoryDao(bookRepository);

		Book book1 = new Book("Central Park", "Guillaume Musso", new ISBN(12));
		Book book2 = new Book("Sauve-moi", "Guillaume Musso", new ISBN(13));
		Book book3 = new Book("Demain", "Guillaume Musso", new ISBN(14));

		List<Book> books = new ArrayList<Book>();
		books.add(book1);
		books.add(book2);
		books.add(book3);
		// ajout d'une liste des livres
		LOGGER.info("Saving a list of book");
		bookRepository.addBooks(books);

		long isbn = 13;
		LocalDate borrowedAt = LocalDate.parse("2019-10-01");
		LOGGER.info("The resident trying to borrow a book with code : " + isbn);
		Book borrowBook = resident.borrowBook(isbn, resident, borrowedAt);

		LOGGER.info("borrowBook " + borrowBook.getTitle());

		long isbn2 = 12;
		LocalDate borrowedAt2 = LocalDate.parse("2019-12-12");
		LOGGER.info("The resident trying to borrow a book with code : " + isbn2);
		Book borrowedBook2 = resident.borrowBook(isbn2, resident, borrowedAt2);
		LOGGER.info("borrowBook2 " + borrowedBook2.getTitle());

		LOGGER.info("The resident return the borrowedBook2  : " + borrowedBook2.getTitle());
		resident.returnBook(borrowedBook2, resident);
		LOGGER.info("Borrowed date before returning the book : " + bookRepository.findBorrowedBookDate(borrowBook));
		LocalDate borrowedAtafter = bookRepository.findBorrowedBookDate(borrowBook);

		long isbn3 = 14;
		LocalDate borrowedAt3 = LocalDate.parse("2019-12-28");
		LOGGER.info("The resident trying to borrow a book with code : " + isbn3);
		Book borrowedBook3 = resident.borrowBook(isbn3, resident, borrowedAt3);
		LOGGER.info("borrowedAt3 " + borrowedBook3.getTitle());

	}
}
