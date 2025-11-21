package com.surest.api.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidLoginExceptionTest {

    @Test
    void constructor_shouldSetMessage() {
        String errorMessage = "Invalid login attempt";
        InvalidLoginException exception = new InvalidLoginException(errorMessage);
        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }
}
