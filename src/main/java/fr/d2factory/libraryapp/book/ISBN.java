package fr.d2factory.libraryapp.book;

public class ISBN {
	long isbnCode;

	public ISBN(long isbnCode) {
		this.isbnCode = isbnCode;
	}

	public long getIsbnCode() {
		return isbnCode;
	}

	public void setIsbnCode(long isbnCode) {
		this.isbnCode = isbnCode;
	}

	public ISBN() {
		super();

	}

}
