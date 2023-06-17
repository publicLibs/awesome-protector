package com.github.publiclibs.awesome.protector.api.audio.exception;

public class LineNotSupportedException extends RuntimeException {
	/**
	 *
	 */
	private static final long serialVersionUID = 1479439293747023877L;

	public LineNotSupportedException() {

	}

	public LineNotSupportedException(final String message) {
		super(message);
	}

	public LineNotSupportedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public LineNotSupportedException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public LineNotSupportedException(final Throwable cause) {
		super(cause);
	}
}