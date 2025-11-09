package com.surest.api.exception;

public class UsernameTakenException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1908171666349238977L;

	public UsernameTakenException(String message) {
        super(message);
    }
}
