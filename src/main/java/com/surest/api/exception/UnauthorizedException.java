package com.surest.api.exception;

public class UnauthorizedException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8281468413426461969L;

	public UnauthorizedException(String message) {
        super(message);
    }
}
