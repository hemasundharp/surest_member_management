package com.surest.api.service.impl;

import com.surest.api.config.JwtTokenUtil;
import com.surest.api.dto.AuthenticationResponse;
import com.surest.api.dto.SignIn;
import com.surest.api.dto.UserDTO;
import com.surest.api.exception.InvalidLoginException;
import com.surest.api.exception.UserNotFoundException;
import com.surest.api.mapper.UserMapper;
import com.surest.api.model.Role;
import com.surest.api.model.User;
import com.surest.api.repository.RoleRepository;
import com.surest.api.repository.UserRepository;
import com.surest.api.service.AuthenticationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private RoleRepository roleRepository;
    @Mock private JwtTokenUtil jwtUtil;
    @Mock private AuthenticationService authenticationService;
    @Mock private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDTO userDTO;
    private Role role;
    private UUID userId;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        role = new Role();
        role.setId(UUID.randomUUID());
        role.setName("ADMIN");

        user = new User();
        user.setId(userId);
        user.setUsername("john");
        user.setPassword("pass");
        user.setRole(role);

        userDTO = new UserDTO();
        userDTO.setUsername("john");
        userDTO.setPassword("pass");
        userDTO.setRoleId(role.getId());
    }

    @Test
    void authenticateUser_success() {
        SignIn login = new SignIn();
        login.setUsername("john");
        login.setPassword("pass");

        when(authenticationService.authenticateWithCredentials(eq("john"), eq("pass")))
                .thenReturn(authentication);

        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtUtil.generateAccessToken(user)).thenReturn("jwt-token");

        AuthenticationResponse response = userService.authenticateUser(login);

        assertNotNull(response);
        assertEquals("john", response.getUsername());
    }

    @Test
    void authenticateUser_invalidCredentials() {
        SignIn login = new SignIn();
        login.setUsername("john");
        login.setPassword("wrong");

        when(authenticationService.authenticateWithCredentials(eq("john"), eq("wrong")))
                .thenThrow(new InvalidLoginException("Invalid credentials"));

        assertThrows(InvalidLoginException.class, () -> userService.authenticateUser(login));
    }


    @Test
    void createUser_success() {
        when(userMapper.toEntity(userDTO)).thenReturn(user);
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDTO);

        UserDTO result = userService.createUser(userDTO);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getAllUsers_success() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(userDTO);

        List<UserDTO> result = userService.getAllUsers();

        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDTO);

        UserDTO result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals("john", result.getUsername());
    }

    @Test
    void getUserById_notFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(userId));
    }

    @Test
    void updateUser_success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDTO);

        UserDTO result = userService.updateById(userId, userDTO);

        assertNotNull(result);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_notFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.updateById(userId, userDTO));
    }

    @Test
    void deleteUser_success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteById(userId);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_notFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.deleteById(userId));
    }

    @Test
    void authenticateUser_ShouldThrowInvalidLoginException_WhenBadCredentials() {
        SignIn login = new SignIn();
        login.setUsername("user");
        login.setPassword("wrong");

        Mockito.when(authenticationService.authenticateWithCredentials("user", "wrong"))
                .thenThrow(new BadCredentialsException("Invalid"));

        assertThrows(InvalidLoginException.class,
                () -> userService.authenticateUser(login));
    }
    @Test
    void updateById_ShouldNotUpdatePassword_WhenPasswordIsNullOrBlank() {
        UUID id = UUID.randomUUID();

        User existing = new User();
        existing.setId(id);
        existing.setUsername("old");

        UserDTO dto = new UserDTO();
        dto.setUsername("new");
        dto.setPassword("");  // BLANK → should skip

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(existing);
        Mockito.when(userMapper.toDto(existing)).thenReturn(dto);

        userService.updateById(id, dto);

        Mockito.verify(passwordEncoder, Mockito.never()).encode(Mockito.anyString());
    }
    @Test
    void updateById_ShouldNotUpdateRole_WhenRoleIdIsNull() {
        UUID id = UUID.randomUUID();

        User existing = new User();
        existing.setId(id);
        existing.setUsername("old");

        UserDTO dto = new UserDTO();
        dto.setUsername("new");
        dto.setRoleId(null);  // → should skip

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(userRepository.save(existing)).thenReturn(existing);
        Mockito.when(userMapper.toDto(existing)).thenReturn(dto);

        userService.updateById(id, dto);

        Mockito.verify(roleRepository, Mockito.never()).findById(Mockito.any());
    }

}
