package com.epicsagaonline.bukkit;

/**
 * Exception thrown when there is an error enabling a plugin.
 * @author _sir_maniac
 *
 */
public class EnableError extends Exception {
	private static final long serialVersionUID = 1L;

	public EnableError() {
		super();
	}

	public EnableError(String message, Throwable cause) {
		super(message, cause);
	}

	public EnableError(String message) {
		super(message);
	}

	public EnableError(Throwable cause) {
		super(cause);
	}

}
