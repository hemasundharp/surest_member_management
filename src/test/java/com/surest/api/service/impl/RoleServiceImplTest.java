package com.surest.api.service.impl;

import com.surest.api.dto.RoleDTO;
import com.surest.api.exception.ResourceNotFoundException;
import com.surest.api.mapper.RoleMapper;
import com.surest.api.model.Role;
import com.surest.api.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    void createRole_success() {
        RoleDTO dto = new RoleDTO();
        dto.setName("Admin");

        Role role = new Role();
        role.setName("Admin");
        role.setId(UUID.randomUUID());

        when(roleMapper.toEntity(dto)).thenReturn(role);
        when(roleRepository.save(role)).thenReturn(role);

        Role result = roleService.createRole(dto);

        assertNotNull(result);
        verify(roleMapper).toEntity(dto);
        verify(roleRepository).save(role);
    }

    @Test
    void getAllRoles_success() {
        Role role = new Role();
        List<Role> list = List.of(role);

        when(roleRepository.findAll()).thenReturn(list);

        List<Role> result = roleService.getAllRoles();

        assertEquals(1, result.size());
        verify(roleRepository).findAll();
    }

    @Test
    void getRoleById_success() {
        UUID id = UUID.randomUUID();
        Role role = new Role();
        role.setId(id);

        when(roleRepository.findById(id)).thenReturn(Optional.of(role));

        Role result = roleService.getRoleById(id);

        assertNotNull(result);
        verify(roleRepository).findById(id);
    }

    @Test
    void getRoleById_notFound() {
        UUID id = UUID.randomUUID();

        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> roleService.getRoleById(id));
    }

    @Test
    void updateRoleById_success() {
        UUID id = UUID.randomUUID();
        RoleDTO dto = new RoleDTO();
        dto.setName("Updated");

        Role existing = new Role();
        existing.setId(id);
        existing.setName("Old");

        when(roleRepository.findById(id)).thenReturn(Optional.of(existing));
        when(roleRepository.save(existing)).thenReturn(existing);

        Role result = roleService.updateById(id, dto);

        assertNotNull(result);
        verify(roleRepository).save(existing);
    }

    @Test
    void updateRoleById_notFound() {
        UUID id = UUID.randomUUID();
        RoleDTO dto = new RoleDTO();
        dto.setName("New");

        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> roleService.updateById(id, dto));
    }

    @Test
    void deleteById_success() {
        UUID id = UUID.randomUUID();

        when(roleRepository.existsById(id)).thenReturn(true);

        roleService.deleteById(id);

        verify(roleRepository).deleteById(id);
    }

    @Test
    void deleteById_notFound() {
        UUID id = UUID.randomUUID();

        when(roleRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> roleService.deleteById(id));
    }
}
