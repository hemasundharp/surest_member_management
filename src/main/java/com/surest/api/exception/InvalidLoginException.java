package com.surest.api.exception;

public class InvalidLoginException extends RuntimeException {

	private static final long serialVersionUID = -8257975190777817864L;

	public InvalidLoginException(String message) {
        super(message);
    }
}
