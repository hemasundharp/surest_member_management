package com.surest.api.controller;

import com.surest.api.dto.UserDTO;
import com.surest.api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserDTO userDTO;
    private UUID userId;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        userDTO = new UserDTO();
        userDTO.setId(userId);
        userDTO.setUsername("testuser");
        userDTO.setPassword("password");
        userDTO.setRoleId(Set.of(UUID.randomUUID()));
        userDTO.setRoleName(Set.of("USER"));
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
        assertNotNull(response.getBody());
        assertEquals(userDTO.getId(), response.getBody().getId());
        verify(userService).getUserById(userId);
    }

    @Test
    void getById_userNull_returnsOkWithNullBody() {
        when(userService.getUserById(userId)).thenReturn(null);
        ResponseEntity<UserDTO> response = userController.getById(userId);
        assertEquals(200, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(userService).getUserById(userId);
    }

    @Test
    void updateById_success() {
        when(userService.updateById(eq(userId), any(UserDTO.class))).thenReturn(userDTO);
        ResponseEntity<UserDTO> response = userController.updateById(userId, userDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
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
}
