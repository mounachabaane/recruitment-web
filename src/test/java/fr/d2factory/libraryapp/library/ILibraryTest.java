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

import fr.d2factory.libraryapp.TownsvilleLibraryApp;
import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.book.IBookRepositoryDao;
import fr.d2factory.libraryapp.exceptions.HasLateBooksException;
import fr.d2factory.libraryapp.exceptions.HasNotEnoughMoneyException;
import fr.d2factory.libraryapp.exceptions.NotFoundBookException;
import fr.d2factory.libraryapp.member.Resident;
import fr.d2factory.libraryapp.member.Student;
import fr.d2factory.libraryapp.utils.IdGenerator;

/**
 * Do not forget to consult the README.md :)
 */

public class ILibraryTest {
	// private ILibrary library = new Resident();
	private IBookRepositoryDao bookRepository = new BookRepository();
	private static List<Book> books;
	private Resident resident = new Resident("Anna", 60, false);
	private Student student = new Student("Alice", 50, false, false);
	private IdGenerator idGenerator = new IdGenerator();
	private static final Logger LOGGER = LoggerFactory.getLogger(ILibraryTest.class);

	public static final float RSIDENT_PRICE_BEFORE_LATE = 0.1f;
	public static final float RESIDENT_PRICE_AFTER_LATE = 0.2f;
	public static final float STUDENT_PRICE = 0.1f;
	public static final int FREE_DAYS_DURATION = 15;
	public static final int RESIDENT_DAYS_BEFORE_LATE = 60;

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
		LOGGER.info(
				"to check jaCoCo Report, please go to /townsville-library/target/site/jacoco/index.html and choose open with <<Web Browser>>");
		LOGGER.info(
				"JaCoCo is an actively developed line coverage tool, that is used to measure how many lines of our code are tested. ");
	}

	@Test
	void member_can_borrow_a_book_if_book_is_available() {
		LOGGER.info("Testing: member_can_borrow_a_book_if_book_is_available");

		long isbnCode = 46578964;
		LocalDate borrowedAt = LocalDate.parse("2019-12-17");
		Book book = resident.borrowBook(isbnCode, resident, borrowedAt);
		assertEquals(isbnCode, book.getIsbn().getIsbnCode());
		assertEquals(true, resident.getBookList().contains(book));
		assertEquals(resident.toString(), book.getMember().toString());

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
		float initWallet = resident.getWallet();
		resident.payBook(numberOfdays);
		float sum = initWallet - resident.getWallet();
		assertEquals(numberOfdays * RSIDENT_PRICE_BEFORE_LATE, sum);

	}

	@Test
	void students_pay_10_cents_the_first_30days() {
		LOGGER.info("Testing: students_pay_10_cents_the_first_30days");
		float initWallet = student.getWallet();
		int numberofDays = 30;
		student.setFreeDays(false);
		student.payBook(numberofDays);

		float pay = initWallet - student.getWallet();

		assertEquals(numberofDays * STUDENT_PRICE, pay);

	}

	@Test
	void students_in_1st_year_are_not_taxed_for_the_first_15days() {
		LOGGER.info("Testing: students_in_1st_year_are_not_taxed_for_the_first_15days");
		student.setFreeDays(true);
		float initWallet = student.getWallet();
		int numberofDays = 22;
		student.payBook(numberofDays);

		float pay = initWallet - student.getWallet();

		assertEquals((String.format("%.02f", (numberofDays - FREE_DAYS_DURATION) * STUDENT_PRICE)),
				String.format("%.02f", pay));

	}

	@Test
	void residents_pay_20cents_for_each_day_they_keep_a_book_after_the_initial_60days(

	) {
		LOGGER.info("Testing: residents_pay_20cents_for_each_day_they_keep_a_book_after_the_initial_60days");
		float initWallet = resident.getWallet();
		int numberOfdays = 65;

		resident.payBook(numberOfdays);
		float sum = initWallet - resident.getWallet();
		float toPayexpected = (float) ((RESIDENT_DAYS_BEFORE_LATE * RSIDENT_PRICE_BEFORE_LATE)
				+ ((numberOfdays - RESIDENT_DAYS_BEFORE_LATE) * RESIDENT_PRICE_AFTER_LATE));
		assertEquals(toPayexpected, sum);
	}

	@Test
	void residents_cannot_borrow_book_if_they_have_late_books() {
		LOGGER.info("Testing:residents_cannot_borrow_book_if_they_have_late_books");
		long isbn5 = 332645646;
		String message = "";
		resident.setLate(true);
		LocalDate borrowedAt = LocalDate.parse("2019-10-01");
		try {

			resident.borrowBook(isbn5, resident, borrowedAt);
		} catch (HasLateBooksException e) {

			// assert (e.getMessage().equals("You already have a book that are late"));
			message = e.getMessage();
		}
		assert (message.equals("You have already a late book!"));
	}

	@Test
	void resident_return_borrowed_book_if_it_available() {
		LOGGER.info("Testing: resident_return_borrowed_book_if_it_available");
		long isbn2 = 465789453;

		LocalDate borrowedAt = LocalDate.parse("2019-10-02");

		Book borrowedBook = resident.borrowBook(isbn2, resident, borrowedAt);

		assertEquals(borrowedAt, bookRepository.findBorrowedBookDate(borrowedBook));
		resident.returnBook(borrowedBook, resident);

		assertNotEquals(borrowedAt, bookRepository.findBorrowedBookDate(borrowedBook));
		assertEquals(false, resident.getBookList().contains(borrowedBook));

	}

	@Test
	void resident_Has_Not_Enough_Of_Money_To_Pay_Book() {
		LOGGER.info("Testing: resident_Has_Not_Enough_Of_Money_To_Pay_Book");
		long isbn5 = 332645646;
		String message = "";
		resident.setWallet(0.2f);
		LocalDate borrowedAt = LocalDate.parse("2019-12-15");
		try {

			Book borrowedBook = resident.borrowBook(isbn5, resident, borrowedAt);
			resident.returnBook(borrowedBook, resident);
		} catch (HasNotEnoughMoneyException e) {

			message = e.getMessage();
		}
		assert (message.equals("You don't have enough of money to pay!"));
	}

	@Test
	void resident_cannot_borrow_book_because_its_not_found() {
		LOGGER.info("Testing: resident_cannot_borrow_book_because_its_not_found");
		long isbn5 = 332645;
		String message = "";

		LocalDate borrowedAt = LocalDate.parse("2019-12-22");
		try {

			resident.borrowBook(isbn5, resident, borrowedAt);

		} catch (NotFoundBookException e) {

			message = e.getMessage();
		}
		assert (message.equals("The book you want to borrow is not found!"));
	}

	@Test
	void student_can_borrow_a_book_if_book_is_available() {
		LOGGER.info("Testing: student_can_borrow_a_book_if_book_is_available");

		long isbnCode = 46578964;
		LocalDate borrowedAt = LocalDate.parse("2019-12-17");
		Book book = student.borrowBook(isbnCode, student, borrowedAt);
		assertEquals(isbnCode, book.getIsbn().getIsbnCode());
		assertEquals(student.toString(), book.getMember().toString());

	}

	@Test
	void student_cannot_borrow_book_beacause_its_not_found() {
		LOGGER.info("Testing: student_cannot_borrow_book_beacause_its_not_found");
		long isbn5 = 3326;
		String message = "";

		LocalDate borrowedAt = LocalDate.parse("2019-12-15");
		try {

			student.borrowBook(isbn5, student, borrowedAt);

		} catch (NotFoundBookException e) {

			message = e.getMessage();
		}
		assert (message.equals("The book you want to borrow is not found!"));
	}

	@Test
	void student_cannot_borrow_book_if_they_have_late_books() {
		LOGGER.info("Testing: student_cannot_borrow_book_if_they_have_late_books");
		long isbn5 = 332645646;
		String message = "";
		student.setLate(true);
		LocalDate borrowedAt = LocalDate.parse("2019-10-01");
		try {

			student.borrowBook(isbn5, student, borrowedAt);
		} catch (HasLateBooksException e) {

			// assert (e.getMessage().equals("You have already a late book!"));
			message = e.getMessage();
		}
		assert (message.equals("You have already a late book!"));
	}

	@Test
	void student_return_borrowed_book_if_it_available() {
		LOGGER.info("Testing: student_return_borrowed_book_if_it_available");
		long isbn2 = 465789453;

		LocalDate borrowedAt = LocalDate.parse("2019-10-01");

		Book borrowedBook = student.borrowBook(isbn2, student, borrowedAt);

		assertEquals(borrowedAt, bookRepository.findBorrowedBookDate(borrowedBook));
		student.returnBook(borrowedBook, student);

		assertNotEquals(borrowedAt, bookRepository.findBorrowedBookDate(borrowedBook));
		assertEquals(false, student.getBookList().contains(borrowedBook));

	}

	@Test
	void student_Has_Not_Enough_Of_Money_To_Pay_Book() {
		LOGGER.info("Testing: student_Has_Not_Enough_Of_Money_To_Pay_Book");
		long isbn5 = 465789453;
		String message = "";
		student.setWallet(0.3f);
		Book borrowedBook = null;
		LocalDate borrowedAt = LocalDate.parse("2019-12-15");
		try {

			borrowedBook = student.borrowBook(isbn5, student, borrowedAt);
			student.returnBook(borrowedBook, student);
		} catch (HasNotEnoughMoneyException e) {

			message = e.getMessage();
		}
		assert (message.equals("You don't have enough of money to pay!"));

	}
	
	/**
	 * this test is for main method
	 */
	@Test
    public void testMain() {
		
        String [] args = {};
      
        TownsvilleLibraryApp.main(args);
    }
    
	

}
