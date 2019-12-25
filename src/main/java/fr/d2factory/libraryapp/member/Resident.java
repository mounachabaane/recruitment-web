package fr.d2factory.libraryapp.member;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.exceptions.HasLateBooksException;
import fr.d2factory.libraryapp.exceptions.HasNotEnoughMoneyException;
import fr.d2factory.libraryapp.exceptions.NotFoundBookException;

public class Resident extends Member {

	private static final Logger LOGGER = LoggerFactory.getLogger(Resident.class);

	public static final float PRICE_BEFORE_LATE = 0.1f;
	public static final float PRICE_AFTER_LATE = 0.2f;
	public static final int DAYS_BEFORE_LATE = 60;

	public Resident(String memberName, float wallet, boolean late) {
		super(memberName, wallet, late);

	}

	public Resident() {
		super();

	}

	@Override
	public void payBook(int numberOfDays) {
		LOGGER.trace("numberOfDays : " + numberOfDays);
		float bill = 0;

		if (numberOfDays <= DAYS_BEFORE_LATE)

		{
			LOGGER.info("resident is taxed 10cents for each day he keep a book");
			bill = numberOfDays * PRICE_BEFORE_LATE;

		}

		else {
			LOGGER.info("residents pay 20cents for each day they keep a book after the initial 60days");
			bill = (DAYS_BEFORE_LATE * PRICE_BEFORE_LATE) + ((numberOfDays - DAYS_BEFORE_LATE) * PRICE_AFTER_LATE);
			

		}

		LOGGER.debug("member bill is: " + bill);
		LOGGER.info("Charge member if he have money enough");
		if (wallet >= bill)

		{
			LOGGER.debug("initial member wallet = " + wallet);
			wallet = wallet - bill;
			LOGGER.debug("new member wallet = " + wallet);
		}

		else {
			LOGGER.info("You don't have enough of money to pay!");
			throw new HasNotEnoughMoneyException("You don't have enough of money to pay!");

		}

	}

	@Override
	public Book borrowBook(long isbnCode, Member member, LocalDate borrowedAt) throws HasLateBooksException {

		Book book;

		LOGGER.info("check if the resident " + memberName + " has a late book");
		if (bookList.stream()
				.filter(borrowedBook -> durationUtil
						.numberOfDays(bookRepositoryDao.findBorrowedBookDate(borrowedBook)) > DAYS_BEFORE_LATE)
				.findFirst().orElse(null) != null) {

			late = true;
		}
		LOGGER.debug("member late is: " + late);

		if (!member.isLate()) {
			LOGGER.info("check if the book is available ");
			book = bookRepositoryDao.findBook(isbnCode);

			if (book.getIsbn() != null) {
				LOGGER.debug("book is available : " + book.toString());

				bookRepositoryDao.saveBookBorrow(book, borrowedAt);

				bookList.add(book);

				bookRepositoryDao.deleteBook(book);
				LOGGER.info("The book " + book.getTitle() + " is borrowod by " + memberName);
				book.setMember(member);
				return book;

			}

			else {
				LOGGER.info("The book " + book.getTitle() + " is not found");
				throw new NotFoundBookException("The book you want to borrow is not found!");
			}

		}

		else {
			LOGGER.error("This member cannot borrow another book!");
			throw new HasLateBooksException("You have already a late book!");
		}

	}

	@Override
	public void returnBook(Book book, Member member) {

		int numberOfDays = 0;
		if (book.getIsbn() != null) {
			LOGGER.info("Resident " + memberName + " return the borrowed book : " + book.getTitle());

			LocalDate borrowedAt = bookRepositoryDao.findBorrowedBookDate(book);
			if (borrowedAt != null) {

				numberOfDays = durationUtil.numberOfDays(borrowedAt);

				payBook(numberOfDays);

				bookRepositoryDao.addBook(book);
				bookRepositoryDao.removeBorrowedBook(book, borrowedAt);
				book.setMember(null);

				bookList.remove(book);

				if (numberOfDays > DAYS_BEFORE_LATE) {
					late = false;
					LOGGER.trace("resident late : " + late);
				}

			}

		}

	}

}
