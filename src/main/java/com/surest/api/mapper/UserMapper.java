package com.surest.api.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.surest.api.dto.UserDTO;
import com.surest.api.model.Role;
import com.surest.api.model.User;
import com.surest.api.repository.RoleRepository;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class UserMapper {

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    // DTO to Entity mapping
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", expression = "java(mapRole(dto.getRoleId()))")
    @Mapping(target = "password", expression = "java(passwordEncoder.encode(dto.getPassword()))")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract User toEntity(UserDTO dto);

    // Entity to DTO mapping
    @Mapping(target = "roleId", source = "role.id")
    @Mapping(target = "password", ignore = true) // never expose password
    public abstract UserDTO toDto(User user);

    // Helper method for role fetching
    protected Role mapRole(java.util.UUID roleId) {
        if (roleId == null) return null;
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));
    }
}
