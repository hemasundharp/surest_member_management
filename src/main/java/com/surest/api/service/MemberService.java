package com.surest.api.service;

import java.util.UUID;
import com.surest.api.dto.MemberDTO;
import com.surest.api.dto.MemberPaginatedResponse;
import com.surest.api.model.Member;

public interface MemberService {

    Member createMember(MemberDTO dto);

    MemberPaginatedResponse getAllMembers(int page, int size, String sortBy, String sortDir);

    Member getMemberById(UUID id);

    Member updateMemberById(UUID id, MemberDTO dto);

    void deleteMemberById(UUID id);
}
