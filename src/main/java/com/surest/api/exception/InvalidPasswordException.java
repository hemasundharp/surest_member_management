package com.surest.api.exception;

public class InvalidPasswordException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7015346620081452026L;

	public InvalidPasswordException(String message) {
        super(message);
    }
}
