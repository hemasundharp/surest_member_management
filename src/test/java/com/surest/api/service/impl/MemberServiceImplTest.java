package com.surest.api.service.impl;

import com.surest.api.dto.MemberDTO;
import com.surest.api.dto.MemberPaginatedResponse;
import com.surest.api.exception.BusinessServiceException;
import com.surest.api.exception.UserNotFoundException;
import com.surest.api.mapper.MemberMapper;
import com.surest.api.model.Member;
import com.surest.api.repository.MemberRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberServiceImplTest {

    @Mock
    private MemberMapper memberMapper;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    private Member member;
    private MemberDTO memberDTO;
    private UUID memberId;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        memberId = UUID.randomUUID();

        member = new Member();
        member.setId(memberId);
        member.setFirstName("John");
        member.setLastName("Doe");
        member.setEmail("john@example.com");
        member.setDateOfBirth(LocalDate.of(1990, 1, 1));
        member.setCreatedAt(LocalDateTime.now());
        member.setUpdatedAt(LocalDateTime.now());

        memberDTO = new MemberDTO();
        memberDTO.setId(memberId);
        memberDTO.setFirstName("John");
        memberDTO.setLastName("Doe");
        memberDTO.setEmail("john@example.com");
        memberDTO.setDateOfBirth(LocalDate.of(1990, 1, 1));
    }

    @Test
    void createMember_success() {
        when(memberMapper.toEntity(memberDTO)).thenReturn(member);
        when(memberRepository.save(member)).thenReturn(member);
        when(memberMapper.toDto(member)).thenReturn(memberDTO);

        MemberDTO result = memberService.createMember(memberDTO);

        assertNotNull(result);
        assertEquals(memberDTO.getFirstName(), result.getFirstName());
        verify(memberRepository).save(member);
    }

    @Test
    void getAllMembers_success_defaultSort() {
        Page<Member> page = new PageImpl<>(List.of(member));
        when(memberRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(memberMapper.toDto(member)).thenReturn(memberDTO);


        MemberPaginatedResponse response =
                memberService.getAllMembers(0, 10, "firstName", "asc");

        assertNotNull(response);
        assertEquals(1, response.getData().size());
        verify(memberRepository).findAll(any(Pageable.class));
    }

    @Test
    void getAllMembers_invalidSortField_usesDefault() {
        Page<Member> page = new PageImpl<>(List.of(member));
        when(memberRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(memberMapper.toDto(member)).thenReturn(memberDTO);


        MemberPaginatedResponse response =
                memberService.getAllMembers(0, 10, "invalidField", "desc");

        assertNotNull(response);
        assertEquals(1, response.getData().size());
        verify(memberRepository).findAll(any(Pageable.class));
    }

    @Test
    void getAllMembers_descSort() {
        Page<Member> page = new PageImpl<>(List.of(member));
        when(memberRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(memberMapper.toDto(member)).thenReturn(memberDTO);


        MemberPaginatedResponse response =
                memberService.getAllMembers(0, 10, "lastName", "desc");

        assertNotNull(response);
        assertEquals(1, response.getData().size());
        verify(memberRepository).findAll(any(Pageable.class));
    }

    @Test
    void getMemberById_success() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(memberMapper.toDto(member)).thenReturn(memberDTO);

        MemberDTO result = memberService.getMemberById(memberId);

        assertNotNull(result);
        assertEquals(memberDTO.getId(), result.getId());
    }

    @Test
    void getMemberById_notFound() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> memberService.getMemberById(memberId));
    }

    @Test
    void updateMemberById_success() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(memberRepository.save(member)).thenReturn(member);
        when(memberMapper.toDto(member)).thenReturn(memberDTO);

        MemberDTO result = memberService.updateMemberById(memberId, memberDTO);

        assertNotNull(result);
        assertEquals(memberDTO.getFirstName(), result.getFirstName());
        verify(memberRepository).save(member);
    }

    @Test
    void updateMemberById_notFound() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> memberService.updateMemberById(memberId, memberDTO));
    }

    @Test
    void deleteMemberById_success() {
        when(memberRepository.existsById(memberId)).thenReturn(true);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        memberService.deleteMemberById(memberId);

        verify(memberRepository).delete(member);
    }

    @Test
    void deleteMemberById_notFound() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(BusinessServiceException.class,
                () -> memberService.deleteMemberById(memberId));
    }
}
