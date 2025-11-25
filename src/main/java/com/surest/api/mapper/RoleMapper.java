package com.surest.api.mapper;

import com.surest.api.dto.RoleDTO;
import com.surest.api.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RoleMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    // DTO to Entity
    Role toEntity(RoleDTO dto);

    // Entity to DTO
    RoleDTO toDto(Role role);
}
