package com.surest.api.service;

import com.surest.api.dto.MemberDTO;
import com.surest.api.dto.MemberPaginatedResponse;

import java.util.UUID;

public interface MemberService {

    MemberDTO createMember(MemberDTO dto);

    MemberPaginatedResponse getAllMembers(int page, int size, String sortBy, String sortDir);

    MemberDTO getMemberById(UUID id);

    MemberDTO updateMemberById(UUID id, MemberDTO dto);

    void deleteMemberById(UUID id);
}
