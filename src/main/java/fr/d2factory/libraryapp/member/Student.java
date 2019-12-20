package fr.d2factory.libraryapp.member;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.exceptions.HasLateBooksException;
import fr.d2factory.libraryapp.exceptions.HasNotEnoughMoneyException;
import fr.d2factory.libraryapp.exceptions.NotFoundBookException;

public class Student extends Member {
	private static final Logger LOGGER = LoggerFactory.getLogger(Student.class);
	private boolean freeDays;
	public static final int STUDENT_PRICE = 10;
	public static final int FREE_DAYS_DURATION = 15;
	public static final int DAYS_BEFORE_LATE = 30;

	public Student(String memberName, float wallet, boolean late, boolean freeDays) {
		super(memberName, wallet, late);
		this.freeDays = freeDays;
	}

	public Student() {
		super();

	}

	@Override
	public void payBook(int numberOfDays) {

		LOGGER.trace("numberOfDays : " + numberOfDays);
		float pay = 0;

		LOGGER.info("students pay 10 cents the first 30days");
		if (!this.freeDays) {
			pay = (float) (pay + (numberOfDays * STUDENT_PRICE));

		}

		LOGGER.info("students in 1st year are not taxed for the first 15days");
		if (this.freeDays) {

			pay = (float) (pay + (numberOfDays - FREE_DAYS_DURATION) * STUDENT_PRICE);

		}

		// check if the book returned if it was late
		if (numberOfDays > DAYS_BEFORE_LATE) {
			LOGGER.info("The book returned if it was late");
			late = false;
		}

		// Charge member if he have money enough
		LOGGER.info("Charge member if he have money enough");
		if (wallet >= pay)

		{
			wallet = wallet - pay;
		}

		else {
			LOGGER.info("You don't have enough of money to pay!");
			throw new HasNotEnoughMoneyException("You don't have enough of money to pay!");

		}

	}

	@Override
	public Book borrowBook(long isbnCode, Member member, LocalDate borrowedAt) {
		List<Book> studentBorrowedBookList = getBookList();
		Book book;

		LOGGER.info("check if the student " + memberName + " has a late book");
		if (studentBorrowedBookList.stream()
				.filter(borrowedBook -> durationUtil
						.numberOfDays(bookRepositoryDao.findBorrowedBookDate(borrowedBook)) > DAYS_BEFORE_LATE)
				.findFirst().orElse(null) != null) {
			late = true;
		}
		LOGGER.debug("member late is: " + late);
		if (!late) {
			LOGGER.info("check if the book is available ");
			book = bookRepositoryDao.findBook(isbnCode);

			if (book.getIsbn() != null) {
				LOGGER.debug("book is available : " + book.getTitle());
				bookRepositoryDao.saveBookBorrow(book, borrowedAt);

				studentBorrowedBookList.add(book);
				setBookList(studentBorrowedBookList);

				bookRepositoryDao.deleteBook(book);

			}
			
			else {
				LOGGER.info("The book " + book.getTitle() + " is not found");
				throw new NotFoundBookException("The book you want to borrow is not found!");
			}

			LOGGER.info("The book " + book.getTitle() + " is borrowod by " + memberName);
		} else {
			LOGGER.error("This member cannot borrow another book!");
			throw new HasLateBooksException("You have already a late book!");
		}

		

		return book;

	}

	@Override
	public void returnBook(Book book, Member member) {

		int numberOfDays = 0;
		LocalDate borrowedAt = bookRepositoryDao.findBorrowedBookDate(book);
		if (borrowedAt != null) {
			LOGGER.info("The student " + memberName + "want to return the book : " + book.getTitle());
			numberOfDays = durationUtil.numberOfDays(borrowedAt);

			payBook(numberOfDays);
			bookRepositoryDao.addBook(book);
			bookRepositoryDao.removeBorrowedBook(book, borrowedAt);
		}

	}

	public boolean isFreeDays() {
		return freeDays;
	}

	public void setFreeDays(boolean freeDays) {
		this.freeDays = freeDays;
	}

}
