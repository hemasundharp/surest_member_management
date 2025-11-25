package com.surest.api.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccessDeniedExceptionTest {
    @Test
    void testExceptionMessage() {
        String msg = "Access denied!";
        AccessDeniedException ex = new AccessDeniedException(msg);
        assertEquals(msg, ex.getMessage());
    }
}