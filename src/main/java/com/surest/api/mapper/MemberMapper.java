package com.surest.api.mapper;

import com.surest.api.dto.MemberDTO;
import com.surest.api.model.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);

    // ✅ Map entity → DTO
    @Mapping(source = "dateOfBirth", target = "dob")
    MemberDTO toDto(Member member);

    // ✅ Map DTO → entity
    @Mapping(source = "dob", target = "dateOfBirth")
    Member toEntity(MemberDTO dto);
}
