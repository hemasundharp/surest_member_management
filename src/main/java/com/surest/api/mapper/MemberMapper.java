package com.surest.api.mapper;

import com.surest.api.dto.MemberDTO;
import com.surest.api.model.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    // Map Entity to DTO
    MemberDTO toDto(Member member);

    // Map DTO to Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Member toEntity(MemberDTO dto);
}
