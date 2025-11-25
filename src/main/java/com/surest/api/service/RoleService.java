package com.surest.api.service;

import java.util.List;
import java.util.UUID;

import com.surest.api.dto.RoleDTO;
import com.surest.api.model.Role;

public interface RoleService {

    Role createRole(RoleDTO dto);

    List<Role> getAllRoles();

    Role getRoleById(UUID id);

    Role updateById(UUID id, RoleDTO dto);

    void deleteById(UUID id);
}
