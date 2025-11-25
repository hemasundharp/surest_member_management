package com.surest.api.exception;

import org.springframework.http.HttpStatus;

public class BusinessServiceException extends RuntimeException {

    private final HttpStatus status;

    public BusinessServiceException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}