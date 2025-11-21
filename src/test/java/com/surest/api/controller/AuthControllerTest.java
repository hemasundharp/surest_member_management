package com.surest.api.controller;

import com.surest.api.dto.AuthenticationResponse;
import com.surest.api.dto.CommonResponseDTO;
import com.surest.api.dto.SignIn;
import com.surest.api.exception.InvalidLoginException;
import com.surest.api.model.Role;
import com.surest.api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private SignIn signIn;
    private AuthenticationResponse authResponse;
    private Role role;

    @BeforeEach
    void setup() {
        role = new Role();
        role.setId(UUID.randomUUID());
        role.setName("ADMIN");

        signIn = new SignIn();
        signIn.setUsername("john");
        signIn.setPassword("pass");

        authResponse = new AuthenticationResponse("john", "jwt-token", UUID.randomUUID(), List.of(role.getName()));
    }

    @Test
    void login_success() {
        when(userService.authenticateUser(signIn)).thenReturn(authResponse);

        ResponseEntity<CommonResponseDTO<AuthenticationResponse>> response = authController.login(signIn);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Login successful", response.getBody().getMessage());
        assertEquals(authResponse, response.getBody().getData());
    }

    @Test
    void login_nullResponse() {
        when(userService.authenticateUser(signIn)).thenReturn(null);

        ResponseEntity<CommonResponseDTO<AuthenticationResponse>> response = authController.login(signIn);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Invalid username or password", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void login_invalidCredentials() {
        when(userService.authenticateUser(signIn))
                .thenThrow(new InvalidLoginException("Invalid username or password"));

        ResponseEntity<CommonResponseDTO<AuthenticationResponse>> response = authController.login(signIn);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Invalid username or password", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void login_unexpectedException() {
        when(userService.authenticateUser(signIn))
                .thenThrow(new RuntimeException("DB error"));

        ResponseEntity<CommonResponseDTO<AuthenticationResponse>> response = authController.login(signIn);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("An unexpected error occurred during login", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void login_blankUsername() {
        SignIn invalidSignIn = new SignIn();
        invalidSignIn.setUsername("  "); // blank username
        invalidSignIn.setPassword("pass");

        when(userService.authenticateUser(invalidSignIn)).thenReturn(null);

        ResponseEntity<CommonResponseDTO<AuthenticationResponse>> response = authController.login(invalidSignIn);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void login_blankPassword() {
        SignIn invalidSignIn = new SignIn();
        invalidSignIn.setUsername("john");
        invalidSignIn.setPassword("  "); // blank password
        when(userService.authenticateUser(invalidSignIn)).thenReturn(null);
        ResponseEntity<CommonResponseDTO<AuthenticationResponse>> response = authController.login(invalidSignIn);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
    }
}
