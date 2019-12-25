package fr.d2factory.libraryapp.member;

import java.util.ArrayList;
import java.util.List;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.book.IBookRepositoryDao;
import fr.d2factory.libraryapp.library.ILibrary;
import fr.d2factory.libraryapp.utils.DurationUtil;

/**
 * A member is a person who can borrow and return books to a {@link ILibrary} A
 * member can be either a student or a resident
 */
public abstract class Member implements ILibrary {
	/**
	 * An initial sum of money the member has
	 */
	protected String memberName;
	protected float wallet;
	protected boolean late;
	IBookRepositoryDao bookRepositoryDao = new BookRepository();
	protected List<Book> bookList = new ArrayList<Book>();
	protected DurationUtil durationUtil = new DurationUtil();

	
	public Member() {
		super();
		
	}

	public Member(float wallet, boolean late) {
		super();
		this.wallet = wallet;
		this.late = late;
	}
	
	

	public Member(String memberName, float wallet, boolean late) {
		super();
		this.memberName = memberName;
		this.wallet = wallet;
		this.late = late;
	}

	/**
	 * The member should pay their books when they are returned to the library
	 *
	 * @param numberOfDays
	 *            the number of days they kept the book
	 */
	public abstract void payBook(int numberOfDays);

	public float getWallet() {
		return wallet;
	}

	public void setWallet(float wallet) {
		this.wallet = wallet;
	}

	public boolean isLate() {
		return late;
	}

	public void setLate(boolean late) {
		this.late = late;
	}

	public List<Book> getBookList() {
		return bookList;
	}

	public void setBookList(List<Book> bookList) {
		this.bookList = bookList;
	}

	public DurationUtil getDurationUtil() {
		return durationUtil;
	}

	public void setDurationUtil(DurationUtil durationUtil) {
		this.durationUtil = durationUtil;
	}

	public IBookRepositoryDao getBookRepositoryDao() {
		return bookRepositoryDao;
	}

	public void setBookRepositoryDao(IBookRepositoryDao bookRepositoryDao) {
		this.bookRepositoryDao = bookRepositoryDao;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	
}
