package fr.d2factory.libraryapp.library;

/**
 * This exception is thrown when a member who owns late books tries to borrow
 * another book
 */
public class HasLateBooksException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HasLateBooksException(String message) {
		super(message);

		System.out.println("message " + message);

	}

	public HasLateBooksException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public HasLateBooksException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public HasLateBooksException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public HasLateBooksException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	
	

}
