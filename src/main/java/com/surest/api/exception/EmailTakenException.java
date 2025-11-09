package com.surest.api.exception;

public class EmailTakenException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -7352506903696860945L;

	public EmailTakenException(String message) {
        super(message);
    }
}
