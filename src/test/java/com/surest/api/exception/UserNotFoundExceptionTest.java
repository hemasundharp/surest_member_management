package com.surest.api.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UserNotFoundExceptionTest {

    @Test
    void testExceptionMessage() {
        String message = "User not found";
        UserNotFoundException ex = new UserNotFoundException(message);
        assertEquals(message, ex.getMessage());
    }
}
