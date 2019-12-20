package fr.d2factory.libraryapp.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.book.BookRepositoryDao;
import fr.d2factory.libraryapp.member.Resident;
import fr.d2factory.libraryapp.member.Student;
import fr.d2factory.libraryapp.utils.IdGenerator;

/**
 * Do not forget to consult the README.md :)
 */

public class ILibraryTest {
	// private ILibrary library = new Resident();
	private BookRepositoryDao bookRepository = new BookRepository();
	private static List<Book> books;
	private Resident resident = new Resident("Anna", 45, false);
	private Student student = new Student("Alice", 50, false, false);
	private IdGenerator idGenerator = new IdGenerator();
	private static final Logger LOGGER = LoggerFactory.getLogger(ILibraryTest.class);

	@BeforeEach
	void setup() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		File booksJson = new File("src/test/resources/books.json");
		books = mapper.readValue(booksJson, new TypeReference<List<Book>>() {
		});

		String uuid = idGenerator.generateUniqueId();
		MDC.put("id", uuid);

		bookRepository.addBooks(books);
		resident.setBookRepositoryDao(bookRepository);
		student.setBookRepositoryDao(bookRepository);
		LOGGER.info("Runing junit tests");
	}

	@Test
	void member_can_borrow_a_book_if_book_is_available() {
		LOGGER.info("Testing: member_can_borrow_a_book_if_book_is_available");

		long isbnCode = 46578964;
		LocalDate borrowedAt = LocalDate.parse("2019-12-17");
		Book book = resident.borrowBook(isbnCode, resident, borrowedAt);
		assertEquals(isbnCode, book.getIsbn().getIsbnCode());

	}

	@Test
	void borrowed_book_is_no_longer_available() {
		LOGGER.info("Testing: borrowed_book_is_no_longer_available");

		resident.setBookRepositoryDao(bookRepository);
		long isbn5 = 332645646;
		LocalDate borrowedAt = LocalDate.parse("2019-10-01");

		Book borrowedBook = resident.borrowBook(isbn5, resident, borrowedAt);

		assertNotEquals(isbn5, bookRepository.findBook(borrowedBook.getIsbn().getIsbnCode()));

	}

	@Test
	void residents_are_taxed_10cents_for_each_day_they_keep_a_book() {
		LOGGER.info("Testing: residents_are_taxed_10cents_for_each_day_they_keep_a_book");
		int numberOfdays = 20;

		resident.payBook(numberOfdays);
		float sum = 45 - resident.getWallet();
		assertEquals(20 * 0.1, sum);

	}

	@Test
	void students_pay_10_cents_the_first_30days() {
		LOGGER.info("Testing: students_pay_10_cents_the_first_30days");
		int numberofDays = 30;
		student.setFreeDays(false);
		student.payBook(numberofDays);

		float pay = 50 - student.getWallet();

		assertEquals(String.format("%.2f", (30 * 0.1)), String.format("%.2f", pay));

	}

	@Test
	void students_in_1st_year_are_not_taxed_for_the_first_15days() {
		LOGGER.info("Testing: students_in_1st_year_are_not_taxed_for_the_first_15days");
		student.setFreeDays(true);
		int numberofDays = 22;
		student.payBook(numberofDays);

		float pay = 50 - student.getWallet();
		assertEquals(String.format("%.2f", 0.7), String.format("%.2f", pay));

	}

	@Test
	void residents_pay_20cents_for_each_day_they_keep_a_book_after_the_initial_60days(

	) {
		LOGGER.info("Testing: residents_pay_20cents_for_each_day_they_keep_a_book_after_the_initial_60days");

		int numberOfdays = 65;

		resident.payBook(numberOfdays);
		float sum = 45 - resident.getWallet();
		float toPayexpected = (float) ((60 * 0.1) + (5 * 0.2));
		assertEquals(toPayexpected, sum);
	}

	@Test
	void members_cannot_borrow_book_if_they_have_late_books() {
		LOGGER.info("Testing: members_cannot_borrow_book_if_they_have_late_books");
		long isbn5 = 332645646;
		String message = "";
		resident.setLate(true);
		LocalDate borrowedAt = LocalDate.parse("2019-10-01");
		try {

			Book borrowedBook = resident.borrowBook(isbn5, resident, borrowedAt);
		} catch (HasLateBooksException e) {

			// assert (e.getMessage().equals("You already have a book that are late"));
			message = e.getMessage();
		}
		assert (message.equals("You have already a late book!"));
	}

	@Test
	void return_borrowed_book_if_it_available() {
		LOGGER.info("Testing: return_borrowed_book_if_it_available");
		long isbn2 = 465789453;

		LocalDate borrowedAt = LocalDate.parse("2019-10-08");

		Book borrowedBook = resident.borrowBook(isbn2, resident, borrowedAt);

		assertEquals(borrowedAt, bookRepository.findBorrowedBookDate(borrowedBook));
		resident.returnBook(borrowedBook, resident);

		assertNotEquals(borrowedAt, bookRepository.findBorrowedBookDate(borrowedBook));

	}

}
