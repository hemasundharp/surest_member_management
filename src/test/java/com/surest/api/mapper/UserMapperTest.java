package com.surest.api.mapper;

import com.surest.api.dto.UserDTO;
import com.surest.api.model.Role;
import com.surest.api.model.User;
import com.surest.api.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserMapperTest {

    private UserMapperImpl userMapper; // Use generated class
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        roleRepository = mock(RoleRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);

        userMapper = new UserMapperImpl();
        userMapper.roleRepository = roleRepository;
        userMapper.passwordEncoder = passwordEncoder;
    }

    @Test
    void testToEntity_mapsRoleAndEncodesPassword() {
        UUID roleId = UUID.randomUUID();
        UserDTO dto = new UserDTO();
        dto.setRoleId(roleId);
        dto.setPassword("plainPassword");

        Role role = new Role();
        role.setId(roleId);
        role.setName("ADMIN");

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");

        User user = userMapper.toEntity(dto);

        assertNotNull(user);
        assertEquals("encodedPassword", user.getPassword());
        assertEquals(role, user.getRole());
        assertNull(user.getId());
        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());
    }

    @Test
    void testToDto_mapsRoleIdAndIgnoresPassword() {
        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setName("USER");

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("john");
        user.setRole(role);
        user.setPassword("secret");

        UserDTO dto = userMapper.toDto(user);

        assertEquals(role.getId(), dto.getRoleId());
        assertNull(dto.getPassword()); // password ignored
        assertEquals("john", dto.getUsername());
    }

    @Test
    void testMapRole_roleFound() {
        UUID roleId = UUID.randomUUID();
        Role role = new Role();
        role.setId(roleId);
        role.setName("ADMIN");

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        Role mappedRole = userMapper.mapRole(roleId);

        assertEquals(role, mappedRole);
    }

    @Test
    void testMapRole_roleNotFound_throwsException() {
        UUID roleId = UUID.randomUUID();
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userMapper.mapRole(roleId));
        assertTrue(ex.getMessage().contains("Role not found"));
    }

    @Test
    void testMapRole_nullRoleId_returnsNull() {
        Role mappedRole = userMapper.mapRole(null);
        assertNull(mappedRole);
    }
}
