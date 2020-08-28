package com.ruthlessjailer.api.theseus.command;

/**
 * @author Vadim Hagedorn
 */
public final class SubCommandException extends RuntimeException {

	private static final long serialVersionUID = 5895339610034949652L;

	public SubCommandException() {
		super();
	}

	public SubCommandException(final String message) {
		super(message);
	}

	public SubCommandException(final Throwable cause) {
		super(cause);
	}

	public SubCommandException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
