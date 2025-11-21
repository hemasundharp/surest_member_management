package com.surest.api.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.surest.api.service.AuthenticationService;

@Service
@AllArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;

    @Override
    public Authentication authenticateWithCredentials(String username, String password) {
        log.info("Attempting authentication for user: {}", username);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            log.info("Authentication successful for user: {}", username);
            return authentication;
        } catch (Exception e) {
            log.warn("Authentication failed for user: {} - Reason: {}", username, e.getMessage());
            throw e;
        }
    }
}
