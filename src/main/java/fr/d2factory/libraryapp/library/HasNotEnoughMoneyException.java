package fr.d2factory.libraryapp.library;

public class HasNotEnoughMoneyException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public HasNotEnoughMoneyException(String message) {
		super(message);

		System.out.println("message " + message);

	}

	public HasNotEnoughMoneyException() {
		super();

	}
}
