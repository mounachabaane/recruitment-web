package fr.d2factory.libraryapp.member;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.d2factory.libraryapp.TownsvilleLibraryApp;
import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.library.HasLateBooksException;

public class Resident extends Member {

	private static final Logger LOGGER = LoggerFactory.getLogger(Resident.class);

	public Resident(String memberName, float wallet, boolean late) {
		super(memberName, wallet, late);

	}

	public Resident() {
		super();

	}

	@Override
	public void payBook(int numberOfDays) {
		LOGGER.trace("numberOfDays : " + numberOfDays);
		float paysum = 0;

		if (numberOfDays <= 60)

		{
			LOGGER.info("resident is taxed 10cents for each day he keep a book");
			paysum = (float) (numberOfDays * 0.10);

		}

		if (numberOfDays > 60) {
			LOGGER.info("residents pay 20cents for each day they keep a book after the initial 60days");
			paysum = (float) ((60 * 0.1) + ((numberOfDays - 60) * 0.20));
			// LOGGER.trace("resident"+ +"late : " + late);
			late = false;
			LOGGER.trace("resident late : " + late);
		}

		LOGGER.info("Charge member if he have money enough");
		if (wallet >= paysum)

		{
			wallet = wallet - paysum;

		}

		else {
			LOGGER.info("You don't have enough of money to pay!");

		}

	}

	@Override
	public Book borrowBook(long isbnCode, Member member, LocalDate borrowedAt) {

		Book book;
		List<Book> residentBorrowedBookList = getBookList();

		LOGGER.info("check if the resident " + memberName + " has a late book");
		if (residentBorrowedBookList.stream().filter(
				borrowedBook -> durationUtil.numberOfDays(bookRepositoryDao.findBorrowedBookDate(borrowedBook)) > 60)
				.findFirst().orElse(null) != null) {

			late = true;
		}
		LOGGER.debug("member late is: " + late);

		if (!member.isLate()) {
			LOGGER.info("check if the book is available ");
			book = bookRepositoryDao.findBook(isbnCode);

			if (book.getIsbn() != null) {
				LOGGER.debug("book is available : " + book.getTitle());

				bookRepositoryDao.saveBookBorrow(book, borrowedAt);
				residentBorrowedBookList.add(book);
				setBookList(residentBorrowedBookList);
				bookRepositoryDao.deleteBook(book);
				return book;
			}

			LOGGER.info("The book " + book.getTitle() + " is borrowod by " + memberName);
		}

		else {
			LOGGER.error("This member cannot borrow another book!");
			throw new HasLateBooksException("You have already a late book!");
		}

		return book;

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

			}

		}

	}

}
