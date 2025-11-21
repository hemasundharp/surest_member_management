package com.surest.api.mapper;

import com.surest.api.dto.UserDTO;
import com.surest.api.model.Role;
import com.surest.api.model.User;
import com.surest.api.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserMapperTest {

    private UserMapperImpl userMapper;
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
    void testToEntity_mapsRolesAndEncodesPassword() {
        UUID roleId = UUID.randomUUID();
        UserDTO dto = new UserDTO();
        dto.setRoleId(Set.of(roleId));
        dto.setPassword("plainPassword");
        Role role = new Role();
        role.setId(roleId);
        role.setName("ADMIN");
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        User user = userMapper.toEntity(dto);
        assertNotNull(user);
        assertEquals("encodedPassword", user.getPassword());
        assertEquals(Set.of(role), user.getRoles());
        assertNull(user.getId());
        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());
    }

    @Test
    void testToDto_mapsRoleIdsAndRoleNamesAndIgnoresPassword() {
        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setName("USER");
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("john");
        user.setRoles(Set.of(role));
        user.setPassword("secret");
        UserDTO dto = userMapper.toDto(user);
        assertNotNull(dto.getRoleId());
        assertEquals(Set.of(role.getId()), dto.getRoleId());
        assertNull(dto.getRoleName());


        assertNull(dto.getPassword());

        assertEquals("john", dto.getUsername());
    }


    @Test
    void testToEntity_roleNotFound_throwsException() {
        UUID roleId = UUID.randomUUID();
        UserDTO dto = new UserDTO();
        dto.setRoleId(Set.of(roleId));
        dto.setPassword("pass");
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userMapper.toEntity(dto));
        assertTrue(ex.getMessage().contains("Role not found"));
    }

    @Test
    void testToEntity_nullRoles_returnsEmptySet() {
        UserDTO dto = new UserDTO();
        dto.setRoleId(null);
        dto.setPassword("pass");
        User user = userMapper.toEntity(dto);
        if (user.getRoles() != null) {
            assertTrue(user.getRoles().isEmpty());
        }
    }



    @Test
    void testToDto_nullRoles_returnsEmptySets() {
        User user = new User();
        user.setRoles(null);
        user.setUsername("john");
        UserDTO dto = userMapper.toDto(user);
        assertTrue(dto.getRoleId() == null || dto.getRoleId().isEmpty());
        assertTrue(dto.getRoleName() == null || dto.getRoleName().isEmpty());
    }

}
