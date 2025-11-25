package com.surest.api.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.surest.api.dto.UserDTO;
import com.surest.api.model.Role;
import com.surest.api.model.User;
import com.surest.api.repository.RoleRepository;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;



@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class UserMapper {

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    // DTO to Entity mapping
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", expression = "java(mapRoles(dto.getRoleId()))")
    @Mapping(target = "password", expression = "java(passwordEncoder.encode(dto.getPassword()))")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    public abstract User toEntity(UserDTO dto);

    // Entity to DTO mapping
    @Mapping(target = "roleId", expression = "java(user.getRoles() != null ? user.getRoles().stream().map(r -> r.getId()).collect(java.util.stream.Collectors.toSet()) : null)")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roleName", ignore = true)
    public abstract UserDTO toDto(User user);


    // Helper method for role fetching
    protected Set<Role> mapRoles(Set<UUID> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) return Collections.emptySet();

        return roleIds.stream()
                .map(id -> roleRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Role not found with ID: " + id)))
                .collect(Collectors.toSet());
    }


}
