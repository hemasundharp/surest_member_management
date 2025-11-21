package com.surest.api.service.impl;

import com.surest.api.dto.RoleDTO;
import com.surest.api.exception.ResourceNotFoundException;
import com.surest.api.mapper.RoleMapper;
import com.surest.api.model.Role;
import com.surest.api.repository.RoleRepository;
import com.surest.api.service.RoleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public Role createRole(RoleDTO dto) {
        log.info("Creating new role: {}", dto.getName());


        if (roleRepository.findByName(dto.getName()).isPresent()) {
            throw new IllegalArgumentException("Role name already exists: " + dto.getName());
        }

        Role role = roleMapper.toEntity(dto);
        Role savedRole = roleRepository.save(role);
        log.info("Role created with ID: {}", savedRole.getId());
        return savedRole;
    }

    @Override
    @Cacheable(value = "roles")
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        log.info("Fetching all roles (may hit database if not cached)");
        List<Role> roles = roleRepository.findAll();
        log.debug("Fetched {} roles from DB", roles.size());
        return roles;
    }

    @Override
    @Cacheable(value = "roles", key = "#id")
    @Transactional(readOnly = true)
    public Role getRoleById(UUID id) {
        log.info("Fetching role by ID: {} (cacheable)", id);
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
    }

    @Override
    @CacheEvict(value = "roles", key = "#id")
    @Transactional
    public Role updateById(UUID id, RoleDTO dto) {
        log.info("Updating role with ID: {}", id);
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        if (roleRepository.findByName(dto.getName())
                .filter(r -> !r.getId().equals(id)) // ignore the current role
                .isPresent()) {
            throw new IllegalArgumentException("Role name already exists: " + dto.getName());
        }
        existingRole.setName(dto.getName());
        Role updated = roleRepository.save(existingRole);
        log.info("Role updated successfully: {}", updated.getId());
        return updated;
    }

    @Override
    @CacheEvict(value = "roles", allEntries = true)
    @Transactional
    public void deleteById(UUID id) {
        log.warn("Deleting role with ID: {}", id);
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role not found with id: " + id);
        }
        roleRepository.deleteById(id);
        log.info("Role deleted successfully and cache evicted for roles");
    }
}
