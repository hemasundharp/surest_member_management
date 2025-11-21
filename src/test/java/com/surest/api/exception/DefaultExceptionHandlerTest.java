package com.surest.api.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultExceptionHandlerTest {

    private DefaultExceptionHandler exceptionHandler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new DefaultExceptionHandler();
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("Mocked request description");
    }

    @Test
    void handleAuthenticationException_returnsUnauthorized() {
        Exception ex = new RuntimeException("Auth failed");
        ResponseEntity<RestError> response = exceptionHandler.handleAuthenticationException(ex);
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Authentication failed at controller advice", response.getBody().getMessage());
        assertEquals("401 UNAUTHORIZED", response.getBody().getDetails());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleInvalidLoginException_returnsUnauthorized() {
        InvalidLoginException ex = new InvalidLoginException("Invalid credentials");
        ResponseEntity<RestError> response = exceptionHandler.handleInvalidLoginException(ex);
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid credentials", response.getBody().getMessage());
        assertEquals("401 UNAUTHORIZED", response.getBody().getDetails());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleAccessDeniedException_returnsForbidden() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");
        ResponseEntity<RestError> response = exceptionHandler.handleAccessDeniedException(ex);
        assertEquals(403, response.getStatusCodeValue());
        assertEquals("Access denied", response.getBody().getMessage());
        assertEquals("403 FORBIDDEN", response.getBody().getDetails());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleUserNotFoundException_returnsNotFound() {
        UserNotFoundException ex = new UserNotFoundException("User not found");
        ResponseEntity<RestError> response = exceptionHandler.handle(ex);
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("User not found", response.getBody().getMessage());
        assertEquals("404 NOT_FOUND", response.getBody().getDetails());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void myAnyExceptionHandler_returnsBadRequest() {
        Exception ex = new Exception("Some error");
        ResponseEntity<RestError> response = exceptionHandler.myAnyExceptionHandler(ex, webRequest);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Some error", response.getBody().getMessage());
        assertEquals("Mocked request description", response.getBody().getDetails());
        assertNotNull(response.getBody().getTimestamp());
    }
}
