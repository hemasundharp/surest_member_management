package com.surest.api.exception;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RestErrorTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        RestError error = new RestError();
        LocalDateTime now = LocalDateTime.now();

        error.setTimestamp(now);
        error.setMessage("Test message");
        error.setDetails("Test details");

        assertEquals(now, error.getTimestamp());
        assertEquals("Test message", error.getMessage());
        assertEquals("Test details", error.getDetails());
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        RestError error = new RestError(now, "Error occurred", "Some details");

        assertEquals(now, error.getTimestamp());
        assertEquals("Error occurred", error.getMessage());
        assertEquals("Some details", error.getDetails());
    }
}
