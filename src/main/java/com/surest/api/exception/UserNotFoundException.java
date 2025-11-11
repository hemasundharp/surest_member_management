package com.surest.api.exception;

public class UserNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 2625724437627888945L;

	public UserNotFoundException(String message) {
        super(message);
    }
}
