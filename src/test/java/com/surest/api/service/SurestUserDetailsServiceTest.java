package com.surest.api.service;

import com.surest.api.model.User;
import com.surest.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SurestUserDetailsServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SurestUserDetailsService userDetailsService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_existingUser_returnsUser() {
        User user = new User();
        user.setUsername("john");
        when(userRepository.findByUsername("john")).thenReturn(java.util.Optional.of(user));

        User result = userDetailsService.loadUserByUsername("john");

        assertEquals("john", result.getUsername());
    }

    @Test
    void loadUserByUsername_nonExistingUser_throwsException() {
        when(userRepository.findByUsername("unknown")).thenReturn(java.util.Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("unknown"));
    }

    @Test
    void initialize_adminDoesNotExist_createsAdmin() {
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        userDetailsService.initialize();

        verify(userRepository).save(any(User.class));
    }

    @Test
    void initialize_adminExists_doesNotCreateAdmin() {
        when(userRepository.existsByUsername("admin")).thenReturn(true);

        userDetailsService.initialize();

        verify(userRepository, never()).save(any(User.class));
    }
}