package com.surest.api.mapper;

import com.surest.api.dto.RoleDTO;
import com.surest.api.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    // DTO to Entity
    Role toEntity(RoleDTO dto);

    // Entity to DTO
    RoleDTO toDto(Role role);
}
