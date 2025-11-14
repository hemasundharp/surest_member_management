package com.surest.api.config;

import com.surest.api.model.Role;
import com.surest.api.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenUtilTest {

    private JwtTokenUtil jwtTokenUtil;
    private User user;

    private final String secretKey = "MySuperSecretKeyForTestingPurposes1234567890"; // 256-bit key for HS256

    @BeforeEach
    void setUp() {
        jwtTokenUtil = new JwtTokenUtil();
        ReflectionTestUtils.setField(jwtTokenUtil, "SECRET_KEY", secretKey);

        Role role = new Role();
        role.setName("ADMIN");

        user = new User();
        user.setId(java.util.UUID.randomUUID());
        user.setUsername("testuser");
        user.setRole(role);
    }

    @Test
    void testGenerateAccessToken_NotNull() {
        String token = jwtTokenUtil.generateAccessToken(user);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testExtractUsername_FromToken() {
        String token = jwtTokenUtil.generateAccessToken(user);
        String usernameFromSubject = jwtTokenUtil.extractUsername(token);
        assertEquals(user.getUsername(), usernameFromSubject);
    }

    @Test
    void testGetUsernameFromToken_Claim() {
        String token = jwtTokenUtil.generateAccessToken(user);
        String username = jwtTokenUtil.getUsernameFromToken(token);
        assertEquals(user.getUsername(), username);
    }

    @Test
    void testGetRoleFromToken_Claim() {
        String token = jwtTokenUtil.generateAccessToken(user);
        String role = jwtTokenUtil.getRoleFromToken(token);
        assertEquals("ADMIN", role);
    }

    @Test
    void testGetExpirationDateFromToken() {
        String token = jwtTokenUtil.generateAccessToken(user);
        Date expiration = jwtTokenUtil.getExpirationDateFromToken(token);
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void testValidateToken_ValidToken() {
        String token = jwtTokenUtil.generateAccessToken(user);
        boolean valid = jwtTokenUtil.validateToken(token, user.getUsername());
        assertTrue(valid);
    }

    @Test
    void testValidateToken_InvalidUsername() {
        String token = jwtTokenUtil.generateAccessToken(user);
        boolean valid = jwtTokenUtil.validateToken(token, "wrongusername");
        assertFalse(valid);
    }

    @Test
    void testValidateToken_InvalidToken() {
        boolean valid = jwtTokenUtil.validateToken("invalid.token.here", user.getUsername());
        assertFalse(valid);
    }

    @Test
    void testValidateToken_ExpiredToken() throws InterruptedException {
        JwtTokenUtil shortLivedTokenUtil = new JwtTokenUtil() {
            @Override
            public String generateAccessToken(User user) {
                ReflectionTestUtils.setField(this, "SECRET_KEY", secretKey);
                return Jwts.builder()
                        .setSubject(user.getId() + "," + user.getUsername())
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis() + 100)) // 0.1s expiry
                        .claim("name", user.getUsername())
                        .claim("role", user.getRole().getName())
                        .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                        .compact();
            }
        };

        String token = shortLivedTokenUtil.generateAccessToken(user);
        Thread.sleep(200); // wait until expired
        boolean valid = jwtTokenUtil.validateToken(token, user.getUsername());
        assertFalse(valid);
    }
    @Test
    void testValidateToken_WrongUsername() {
        String token = jwtTokenUtil.generateAccessToken(user);
        boolean valid = jwtTokenUtil.validateToken(token, "wrongUsername");
        assertFalse(valid);
    }

    @Test
    void testValidateToken_ExpiredTokenBranch() throws InterruptedException {
        JwtTokenUtil shortLivedTokenUtil = new JwtTokenUtil() {
            @Override
            public String generateAccessToken(User user) {
                ReflectionTestUtils.setField(this, "SECRET_KEY", secretKey);
                return Jwts.builder()
                        .setSubject(user.getId() + "," + user.getUsername())
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis() + 50)) // 50ms expiry
                        .claim("name", user.getUsername())
                        .claim("role", user.getRole().getName())
                        .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                        .compact();
            }
        };

        String token = shortLivedTokenUtil.generateAccessToken(user);
        Thread.sleep(100);
        boolean valid = jwtTokenUtil.validateToken(token, user.getUsername());
        assertFalse(valid);
    }

}
