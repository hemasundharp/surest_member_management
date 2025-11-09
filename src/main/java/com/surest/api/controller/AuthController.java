package com.surest.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.surest.api.dto.AuthenticationResponse;
import com.surest.api.dto.CommonResponseDTO;
import com.surest.api.dto.SignIn;
import com.surest.api.service.UserService;
import com.surest.api.exception.InvalidLoginException;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Tag(name = "Authentication")
@CrossOrigin(origins = "*")
@Slf4j // ✅ Enable SLF4J Logging
public class AuthController {

    private final UserService userService;

    @PostMapping("/signin")
    public ResponseEntity<CommonResponseDTO<AuthenticationResponse>> login(@RequestBody @Valid SignIn loginDto) {
        log.info("🔐 Login attempt for username: {}", loginDto.getUsername());

        try {
            AuthenticationResponse authenticationResponse = userService.authenticateUser(loginDto);

            if (authenticationResponse != null) {
                log.info("✅ Login successful for user: {}", loginDto.getUsername());
                CommonResponseDTO<AuthenticationResponse> successResponse = new CommonResponseDTO<>(
                        true,
                        "Login successful",
                        authenticationResponse
                );
                return ResponseEntity.ok(successResponse);
            } else {
                log.warn("⚠️ Login failed (null response) for user: {}", loginDto.getUsername());
                CommonResponseDTO<AuthenticationResponse> failureResponse = new CommonResponseDTO<>(
                        false,
                        "Invalid username or password",
                        null
                );
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(failureResponse);
            }
        } catch (InvalidLoginException e) {
            log.warn("❌ Invalid credentials for username: {}", loginDto.getUsername());
            CommonResponseDTO<AuthenticationResponse> failureResponse = new CommonResponseDTO<>(
                    false,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(failureResponse);
        } catch (Exception e) {
            log.error("💥 Unexpected error during login for username {}: {}", loginDto.getUsername(), e.getMessage(), e);
            CommonResponseDTO<AuthenticationResponse> errorResponse = new CommonResponseDTO<>(
                    false,
                    "An unexpected error occurred during login",
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
