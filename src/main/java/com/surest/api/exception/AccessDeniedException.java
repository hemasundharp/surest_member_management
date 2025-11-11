package com.surest.api.exception;

public class AccessDeniedException extends RuntimeException {

	private static final long serialVersionUID = 7174748901632365434L;

	public AccessDeniedException(String message) {
        super(message);
    }
}
