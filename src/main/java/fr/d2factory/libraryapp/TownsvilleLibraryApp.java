package fr.d2factory.libraryapp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.book.BookRepositoryDao;
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
		LOGGER.info("***Start testing from main****");
		BookRepositoryDao bookRepository = new BookRepository();

		LOGGER.info("Testing with a resident member who has no late");
		Member resident = new Resident(600, false);

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
		LOGGER.info("The resident trying to borrow a book");
		long isbn = 13;
		LocalDate borrowedAt = LocalDate.parse("2019-10-02");

		Book borrowBook = resident.borrowBook(isbn, resident, borrowedAt);
		System.out.println("borrowBook " + borrowBook.getTitle());

		LocalDate borrowedAtre = bookRepository.findBorrowedBookDate(borrowBook);
		resident.returnBook(borrowBook, resident);
		LocalDate borrowedAtafter = bookRepository.findBorrowedBookDate(borrowBook);

		long isbn5 = 1;
		LocalDate ll = LocalDate.parse("2019-12-12");
		Book borrowBook3 = resident.borrowBook(isbn5, resident, ll);
		System.out.println("borrowBook " + borrowBook3.getTitle());

	}

}
