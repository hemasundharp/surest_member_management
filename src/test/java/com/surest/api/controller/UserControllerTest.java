package com.surest.api.controller;

import com.surest.api.dto.UserDTO;
import com.surest.api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserDTO userDTO;
    private UUID userId;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        userDTO = new UserDTO();
        userDTO.setId(userId);
        userDTO.setUsername("testuser");
        userDTO.setPassword("password");
        userDTO.setRoleId(UUID.randomUUID());
        userDTO.setRoleName("USER");
    }

    @Test
    void createUser_success() {
        when(userService.createUser(any(UserDTO.class))).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.createUser(userDTO);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(userDTO.getId(), response.getBody().getId());
        verify(userService).createUser(userDTO);
    }

    @Test
    void getAllUsers_withUsers() {
        when(userService.getAllUsers()).thenReturn(List.of(userDTO));

        ResponseEntity<List<UserDTO>> response = userController.getAllUsers();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(userService).getAllUsers();
    }

    @Test
    void getAllUsers_noUsers_returnsNoContent() {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        ResponseEntity<List<UserDTO>> response = userController.getAllUsers();

        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(userService).getAllUsers();
    }

    @Test
    void getById_success() {
        when(userService.getUserById(userId)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.getById(userId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(userDTO.getId(), response.getBody().getId());
        verify(userService).getUserById(userId);
    }

    @Test
    void updateById_success() {
        when(userService.updateById(eq(userId), any(UserDTO.class))).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.updateById(userId, userDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(userDTO.getId(), response.getBody().getId());
        verify(userService).updateById(userId, userDTO);
    }

    @Test
    void deleteById_success() {
        doNothing().when(userService).deleteById(userId);

        ResponseEntity<String> response = userController.deleteById(userId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Deleted user successfully", response.getBody());
        verify(userService).deleteById(userId);
    }

    @Test
    void getAllUsers_usersNull_returnsNoContent() {
        when(userService.getAllUsers()).thenReturn(null);

        ResponseEntity<?> response = userController.getAllUsers();

        assertEquals(204, response.getStatusCodeValue());
        verify(userService).getAllUsers();
    }

    @Test
    void getAllUsers_usersEmpty_returnsNoContent() {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = userController.getAllUsers();

        assertEquals(204, response.getStatusCodeValue());
        verify(userService).getAllUsers();
    }

    @Test
    void getById_userNull_logsNotFound() {
        when(userService.getUserById(userId)).thenReturn(null);

        ResponseEntity<UserDTO> response = userController.getById(userId);

        assertEquals(200, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(userService).getUserById(userId);
    }
}
