package fr.d2factory.libraryapp.exceptions;

public class NotFoundBookException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NotFoundBookException(String message) {
		super(message);

		System.out.println("message " + message);

	}

	public NotFoundBookException() {
		super();

	}

}
