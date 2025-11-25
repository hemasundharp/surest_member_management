package com.surest.api.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

import com.surest.api.dto.RoleDTO;
import com.surest.api.model.Role;
import com.surest.api.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

class RoleControllerTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    private Role role;
    private RoleDTO roleDTO;
    private UUID roleId;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        roleId = UUID.randomUUID();
        role = new Role();
        role.setId(roleId);
        role.setName("ADMIN");
        roleDTO = new RoleDTO();
        roleDTO.setName("ADMIN");
    }

    @Test
    void createRole_success() {
        when(roleService.createRole(roleDTO)).thenReturn(role);
        ResponseEntity<Role> response = roleController.createRole(roleDTO);
        assertEquals(CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ADMIN", response.getBody().getName());
        verify(roleService).createRole(roleDTO);
    }

    @Test
    void getAllRoles_successWithRoles() {
        when(roleService.getAllRoles()).thenReturn(List.of(role));
        ResponseEntity<List<Role>> response = roleController.getAllRoles();
        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(roleService).getAllRoles();
    }

    @Test
    void getAllRoles_noRoles() {
        when(roleService.getAllRoles()).thenReturn(Collections.emptyList());
        ResponseEntity<List<Role>> response = roleController.getAllRoles();
        assertEquals(NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(roleService).getAllRoles();
    }

    @Test
    void getById_success() {
        when(roleService.getRoleById(roleId)).thenReturn(role);
        ResponseEntity<Role> response = roleController.getById(roleId);
        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ADMIN", response.getBody().getName());
        verify(roleService).getRoleById(roleId);
    }

    @Test
    void updateById_success() {
        when(roleService.updateById(roleId, roleDTO)).thenReturn(role);
        ResponseEntity<Role> response = roleController.updateById(roleId, roleDTO);
        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ADMIN", response.getBody().getName());
        verify(roleService).updateById(roleId, roleDTO);
    }

    @Test
    void deleteById_success() {
        doNothing().when(roleService).deleteById(roleId);
        ResponseEntity<String> response = roleController.deleteById(roleId);
        assertEquals(OK, response.getStatusCode());
        assertEquals("Role deleted successfully", response.getBody());
        verify(roleService).deleteById(roleId);
    }

    @Test
    void getAllRoles_rolesNullOrEmpty() {
        when(roleService.getAllRoles()).thenReturn(Collections.emptyList());
        ResponseEntity<List<Role>> response = roleController.getAllRoles();
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(roleService).getAllRoles();
    }
    @Test
    void getAllRoles_noRoles_returnsNoContent() {
        when(roleService.getAllRoles()).thenReturn(Collections.emptyList());
        ResponseEntity<List<Role>> response = roleController.getAllRoles();
        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
    @Test
    void getAllRoles_rolesNull_returnsNoContent() {
        when(roleService.getAllRoles()).thenReturn(null);
        ResponseEntity<List<Role>> response = roleController.getAllRoles();
        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

}
