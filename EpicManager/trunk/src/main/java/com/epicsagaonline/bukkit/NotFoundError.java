package com.epicsagaonline.bukkit;

/**
 * Exception thrown when item isn't found in permmissions database.
 * @author _sir_maniac
 *
 */
public class NotFoundError extends Exception {
	private static final long serialVersionUID = 1L;

	public NotFoundError() {
		super();
	}

	public NotFoundError(String message, Throwable cause) {
		super(message, cause);
	}

	public NotFoundError(String message) {
		super(message);
	}

	public NotFoundError(Throwable cause) {
		super(cause);
	}

}
