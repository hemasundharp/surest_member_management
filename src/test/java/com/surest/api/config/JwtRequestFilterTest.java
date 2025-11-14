package com.surest.api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class JwtRequestFilterTest {

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    private JwtRequestFilter jwtRequestFilter;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        jwtRequestFilter = new JwtRequestFilter(jwtTokenUtil);
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_validToken_authenticatesUser() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtTokenUtil.getUsernameFromToken("validToken")).thenReturn("testUser");
        when(jwtTokenUtil.validateToken("validToken", "testUser")).thenReturn(true);
        when(jwtTokenUtil.getRoleFromToken("validToken")).thenReturn("ROLE_USER");

        jwtRequestFilter.doFilterInternal(request, response, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("testUser", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_invalidToken_returnsUnauthorized() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");
        when(jwtTokenUtil.getUsernameFromToken("invalidToken")).thenThrow(new RuntimeException("Token error"));

        StringWriter responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        jwtRequestFilter.doFilterInternal(request, response, chain);

        assertTrue(responseWriter.toString().contains("Unauthorized"));
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void doFilterInternal_noAuthorizationHeader_callsChain() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtRequestFilter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
    @Test
    void doFilterInternal_authorizationHeaderDoesNotStartWithBearer_callsChain() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Basic xyz"); // Not Bearer
        jwtRequestFilter.doFilterInternal(request, response, chain);
        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_usernameIsNull_callsChain() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer someToken");
        when(jwtTokenUtil.getUsernameFromToken("someToken")).thenReturn(null); // simulate username not extracted

        jwtRequestFilter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_validateTokenReturnsFalse_callsChain() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer someToken");
        when(jwtTokenUtil.getUsernameFromToken("someToken")).thenReturn("user");
        when(jwtTokenUtil.validateToken("someToken", "user")).thenReturn(false); // token invalid

        jwtRequestFilter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
    @Test
    void doFilterInternal_authenticationAlreadySet_skipsSettingAuthentication() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer someToken");
        when(jwtTokenUtil.getUsernameFromToken("someToken")).thenReturn("user");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("existingUser", null, Collections.emptyList())
        );

        jwtRequestFilter.doFilterInternal(request, response, chain);

        assertEquals("existingUser", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(chain).doFilter(request, response);
    }

}
