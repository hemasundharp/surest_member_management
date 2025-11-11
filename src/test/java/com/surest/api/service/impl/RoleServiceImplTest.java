package com.surest.api.service.impl;

import com.surest.api.dto.RoleDTO;
import com.surest.api.mapper.RoleMapper;
import com.surest.api.model.Role;
import com.surest.api.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTest {
    @Mock
    private RoleMapper roleMapper;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private RoleServiceImpl roleServiceImpl;

    @Test
    void createRoleTest(){
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setName("admin");
        Role role = new Role();
        role.setName("admin");
        when(roleMapper.toEntity(any(RoleDTO.class))).thenReturn(role);
        role.setId(UUID.randomUUID());
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        Role result = roleServiceImpl.createRole(roleDTO);
        assertNotNull(result);

    }
}
