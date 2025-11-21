package com.surest.api.config;

import com.surest.api.model.Role;
import com.surest.api.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenUtilTest {

    private JwtTokenUtil jwtTokenUtil;
    private User user;
    private final String secretKey = "MySuperSecretKeyForTestingPurposes1234567890"; // 256-bit key

    @BeforeEach
    void setUp() {
        jwtTokenUtil = new JwtTokenUtil();
        ReflectionTestUtils.setField(jwtTokenUtil, "SECRET_KEY", secretKey);

        Role role = new Role();
        role.setName("ADMIN");

        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        user.setRoles(Collections.singleton(role));
    }

    @Test
    void testGenerateAccessToken_NotNull() {
        String token = jwtTokenUtil.generateAccessToken(user);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testExtractUserId_FromToken() {
        String token = jwtTokenUtil.generateAccessToken(user);
        String extractedUserId = jwtTokenUtil.extractUserId(token);
        assertEquals(user.getId().toString(), extractedUserId);
    }

    @Test
    void testGetRolesFromToken() {
        String token = jwtTokenUtil.generateAccessToken(user);
        List<String> roles = jwtTokenUtil.getRolesFromToken(token);
        assertNotNull(roles);
        assertEquals(1, roles.size());
        assertTrue(roles.contains("ADMIN"));
    }

    @Test
    void testGetExpirationDateFromToken() {
        String token = jwtTokenUtil.generateAccessToken(user);
        Date expiration = jwtTokenUtil.getExpirationDateFromToken(token);
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void testGetClaimFromToken() {
        String token = jwtTokenUtil.generateAccessToken(user);
        String userId = jwtTokenUtil.getClaimFromToken(token, claims -> claims.getSubject());
        assertEquals(user.getId().toString(), userId);
    }

    @Test
    void testValidateToken_ValidToken() {
        String token = jwtTokenUtil.generateAccessToken(user);
        boolean valid = jwtTokenUtil.validateToken(token, user.getId().toString());
        assertTrue(valid);
    }

    @Test
    void testValidateToken_InvalidUserId() {
        String token = jwtTokenUtil.generateAccessToken(user);
        boolean valid = jwtTokenUtil.validateToken(token, UUID.randomUUID().toString());
        assertFalse(valid);
    }

    @Test
    void testValidateToken_InvalidToken() {
        boolean valid = jwtTokenUtil.validateToken("invalid.token.here", user.getId().toString());
        assertFalse(valid);
    }

    @Test
    void testValidateToken_ExpiredToken() throws InterruptedException {
        JwtTokenUtil shortLivedTokenUtil = new JwtTokenUtil() {
            @Override
            public String generateAccessToken(User user) {
                ReflectionTestUtils.setField(this, "SECRET_KEY", secretKey);
                return Jwts.builder()
                        .setSubject(user.getId().toString())
                        .claim("roles", user.getRoles().stream().map(Role::getName).toList())
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis() + 50)) // 50ms expiry
                        .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                        .compact();
            }
        };

        String token = shortLivedTokenUtil.generateAccessToken(user);
        Thread.sleep(100); // ensure token expired
        boolean valid = jwtTokenUtil.validateToken(token, user.getId().toString());
        assertFalse(valid);
    }

    @Test
    void testExtractUserId_InvalidToken_ThrowsException() {
        assertThrows(Exception.class, () -> jwtTokenUtil.extractUserId("invalid.token"));
    }

    @Test
    void testGetRolesFromToken_InvalidToken_ThrowsException() {
        assertThrows(Exception.class, () -> jwtTokenUtil.getRolesFromToken("invalid.token"));
    }

    @Test
    void testGetExpirationDateFromToken_InvalidToken_ThrowsException() {
        assertThrows(Exception.class, () -> jwtTokenUtil.getExpirationDateFromToken("invalid.token"));
    }

    @Test
    void testValidateToken_NullOrMalformedToken() {
        assertFalse(jwtTokenUtil.validateToken(null, user.getId().toString()));
        assertFalse(jwtTokenUtil.validateToken("", user.getId().toString()));
        assertFalse(jwtTokenUtil.validateToken("malformed.token", user.getId().toString()));
    }
}
