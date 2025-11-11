package com.surest.api.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.surest.api.dto.RoleDTO;
import com.surest.api.model.Role;
import com.surest.api.service.RoleService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Role Management", description = "APIs for managing user roles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RoleController {

    private static final Logger log = LoggerFactory.getLogger(RoleController.class);
    private final RoleService roleService;

    // Only ADMINS can create roles
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody RoleDTO dto) {
        log.info("Request received to create role: {}", dto.getName());
        Role role = roleService.createRole(dto);
        log.info("Role created successfully with ID: {}", role.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }

    // Both ADMIN and USER can read roles
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        log.info("Fetching all roles...");
        List<Role> roles = roleService.getAllRoles();

        if (roles == null || roles.isEmpty()) {
            log.warn("No roles found in the system.");
            return ResponseEntity.noContent().build();
        }

        log.info("Retrieved {} roles successfully.", roles.size());
        return ResponseEntity.ok(roles);
    }

    // Both ADMIN and USER can get a specific role
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<Role> getById(@PathVariable UUID id) {
        log.info("Fetching role by ID: {}", id);
        Role role = roleService.getRoleById(id);
        log.info("Role found: {}", role.getName());
        return ResponseEntity.ok(role);
    }

    // Only ADMIN can update roles
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Role> updateById(@PathVariable UUID id, @RequestBody RoleDTO dto) {
        log.info("Updating role with ID: {} and new name: {}", id, dto.getName());
        Role updatedRole = roleService.updateById(id, dto);
        log.info("Role updated successfully: {}", updatedRole.getName());
        return ResponseEntity.ok(updatedRole);
    }

    // Only ADMIN can delete roles
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable UUID id) {
        log.info("Deleting role with ID: {}", id);
        roleService.deleteById(id);
        log.info("Role deleted successfully with ID: {}", id);
        return ResponseEntity.ok("Role deleted successfully");
    }
}
