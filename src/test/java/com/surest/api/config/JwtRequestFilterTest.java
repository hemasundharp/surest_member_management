package com.surest.api.config;

import com.surest.api.model.User;
import com.surest.api.repository.UserRepository;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtRequestFilterTest {

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @InjectMocks
    private JwtRequestFilter jwtRequestFilter;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_validToken_authenticatesUser() throws ServletException, IOException {
        String token = "validToken";
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setUsername("testUser");
        user.setRoles(Collections.emptySet());

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenUtil.extractUserId(token)).thenReturn(userId.toString());
        when(jwtTokenUtil.validateToken(token, userId.toString())).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        jwtRequestFilter.doFilterInternal(request, response, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("testUser", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_nullUser_doesNotAuthenticate() throws ServletException, IOException {
        String token = "token";
        UUID userId = UUID.randomUUID();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenUtil.extractUserId(token)).thenReturn(userId.toString());
        when(jwtTokenUtil.validateToken(token, userId.toString())).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        jwtRequestFilter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_invalidToken_returnsUnauthorized() throws ServletException, IOException {
        String token = "invalidToken";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenUtil.extractUserId(token)).thenThrow(new RuntimeException("Token error"));

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
    void doFilterInternal_headerNotBearer_callsChain() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Basic xyz");
        jwtRequestFilter.doFilterInternal(request, response, chain);
        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_authenticationAlreadySet_skips() throws ServletException, IOException {
        String token = "token";
        UUID userId = UUID.randomUUID();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenUtil.extractUserId(token)).thenReturn(userId.toString());
        when(jwtTokenUtil.validateToken(token, userId.toString())).thenReturn(true);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("existingUser", null, Collections.emptyList())
        );

        jwtRequestFilter.doFilterInternal(request, response, chain);

        assertEquals("existingUser", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(chain).doFilter(request, response);
    }
}
