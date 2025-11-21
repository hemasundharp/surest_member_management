package com.surest.api.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

import static org.mockito.Mockito.*;

class DelegatedAuthenticationEntryPointTest {

    @Mock
    private HandlerExceptionResolver resolver;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authException;

    private DelegatedAuthenticationEntryPoint entryPoint;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        entryPoint = new DelegatedAuthenticationEntryPoint(resolver);
    }

    @Test
    void testCommenceDelegatesToResolver() throws IOException, ServletException {
        entryPoint.commence(request, response, authException);
        verify(resolver).resolveException(request, response, null, authException);
    }

    @Test
    void testCommenceResolverThrowsException() throws IOException, ServletException {
        doThrow(new RuntimeException("resolver error")).when(resolver).resolveException(any(), any(), any(), any());
        try {
            entryPoint.commence(request, response, authException);
        } catch (RuntimeException ex) {

            assert ex.getMessage().equals("resolver error");
        }
    }
}
