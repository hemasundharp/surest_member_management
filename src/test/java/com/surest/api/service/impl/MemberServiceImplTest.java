package com.surest.api.service.impl;

import com.surest.api.dto.MemberDTO;
import com.surest.api.dto.MemberPaginatedResponse;
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

class MemberServiceTest {

    @Mock
    private MemberMapper memberMapper;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

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

        Member result = memberService.createMember(memberDTO);

        assertNotNull(result);
        verify(memberRepository).save(member);
    }

    @Test
    void getAllMembers_validSortField_ASC() {
        Page<Member> page = new PageImpl<>(List.of(member));
        when(memberRepository.findAll(any(Pageable.class))).thenReturn(page);

        MemberPaginatedResponse response =
                memberService.getAllMembers(0, 10, "lastName", "asc");

        assertEquals(1, response.getData().size());
        verify(memberRepository).findAll(any(Pageable.class));
    }

    @Test
    void getAllMembers_validSortField_DESC() {
        Page<Member> page = new PageImpl<>(List.of(member));
        when(memberRepository.findAll(any(Pageable.class))).thenReturn(page);

        MemberPaginatedResponse response =
                memberService.getAllMembers(0, 10, "email", "desc");

        assertEquals(1, response.getData().size());
    }

    @Test
    void getAllMembers_invalidSortField_defaultsToFirstName() {
        Page<Member> page = new PageImpl<>(List.of(member));
        when(memberRepository.findAll(any(Pageable.class))).thenReturn(page);

        MemberPaginatedResponse response =
                memberService.getAllMembers(0, 10, "unknownField", "asc");

        assertEquals(1, response.getData().size());
    }


    @Test
    void getMemberById_success() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        Member result = memberService.getMemberById(memberId);

        assertNotNull(result);
    }

    @Test
    void getMemberById_notFound() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> memberService.getMemberById(memberId));
    }

    @Test
    void updateMemberById_success() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(memberRepository.save(member)).thenReturn(member);

        Member result = memberService.updateMemberById(memberId, memberDTO);

        assertNotNull(result);
        verify(memberRepository).save(member);
    }

    @Test
    void updateMemberById_memberNotFound() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> memberService.updateMemberById(memberId, memberDTO));
    }

    @Test
    void deleteMemberById_success() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        memberService.deleteMemberById(memberId);

        verify(memberRepository).delete(member);
    }

    @Test
    void deleteMemberById_notFound() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> memberService.deleteMemberById(memberId));
    }

    @Test
    void isSortableField_validFields() throws Exception {
        var method = MemberService.class.getDeclaredMethod("isSortableField", String.class);
        method.setAccessible(true);

        assertTrue((boolean) method.invoke(memberService, "firstName"));
        assertTrue((boolean) method.invoke(memberService, "lastName"));
        assertTrue((boolean) method.invoke(memberService, "email"));
        assertTrue((boolean) method.invoke(memberService, "dateOfBirth"));
        assertTrue((boolean) method.invoke(memberService, "createdAt"));
    }

    @Test
    void isSortableField_invalidField() throws Exception {
        var method = MemberService.class.getDeclaredMethod("isSortableField", String.class);
        method.setAccessible(true);

        assertFalse((boolean) method.invoke(memberService, "randomField"));
    }
    @Test
    void getAllMembers_sortByNull_usesDefaultSort() {
        Page<Member> page = new PageImpl<>(List.of(member));
        when(memberRepository.findAll(any(Pageable.class))).thenReturn(page);

        memberService.getAllMembers(0, 10, null, "asc");

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(memberRepository).findAll(captor.capture());
        Pageable captured = captor.getValue();
        assertNotNull(captured.getSort().getOrderFor("firstName"));
        assertEquals(Sort.Direction.ASC, captured.getSort().getOrderFor("firstName").getDirection());
    }

    @Test
    void getAllMembers_sortByValidField_usesProvidedSort() {
        Page<Member> page = new PageImpl<>(List.of(member));
        when(memberRepository.findAll(any(Pageable.class))).thenReturn(page);

        memberService.getAllMembers(0, 10, "dateOfBirth", "desc");

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(memberRepository).findAll(captor.capture());
        Pageable captured = captor.getValue();
        assertNotNull(captured.getSort().getOrderFor("dateOfBirth"));
        assertEquals(Sort.Direction.DESC, captured.getSort().getOrderFor("dateOfBirth").getDirection());
    }
    @Test
    void getAllMembers_sortBy_firstName() {
        Page<Member> page = new PageImpl<>(List.of(member));
        when(memberRepository.findAll(any(Pageable.class))).thenReturn(page);

        memberService.getAllMembers(0, 10, "firstName", "asc");

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(memberRepository).findAll(captor.capture());
        assertNotNull(captor.getValue().getSort().getOrderFor("firstName"));
    }

    @Test
    void getAllMembers_sortBy_lastName() {
        Page<Member> page = new PageImpl<>(List.of(member));
        when(memberRepository.findAll(any(Pageable.class))).thenReturn(page);

        memberService.getAllMembers(0, 10, "lastName", "asc");

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(memberRepository).findAll(captor.capture());
        assertNotNull(captor.getValue().getSort().getOrderFor("lastName"));
    }

    @Test
    void getAllMembers_sortBy_email() {
        Page<Member> page = new PageImpl<>(List.of(member));
        when(memberRepository.findAll(any(Pageable.class))).thenReturn(page);

        memberService.getAllMembers(0, 10, "email", "asc");

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(memberRepository).findAll(captor.capture());
        assertNotNull(captor.getValue().getSort().getOrderFor("email"));
    }

    @Test
    void getAllMembers_sortBy_dateOfBirth() {
        Page<Member> page = new PageImpl<>(List.of(member));
        when(memberRepository.findAll(any(Pageable.class))).thenReturn(page);

        memberService.getAllMembers(0, 10, "dateOfBirth", "asc");

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(memberRepository).findAll(captor.capture());
        assertNotNull(captor.getValue().getSort().getOrderFor("dateOfBirth"));
    }

    @Test
    void getAllMembers_sortBy_createdAt() {
        Page<Member> page = new PageImpl<>(List.of(member));
        when(memberRepository.findAll(any(Pageable.class))).thenReturn(page);

        memberService.getAllMembers(0, 10, "createdAt", "asc");

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(memberRepository).findAll(captor.capture());
        assertNotNull(captor.getValue().getSort().getOrderFor("createdAt"));
    }
    @Test
    void getAllMembers_sortByNotNullButInvalid_hitsFalseBranch() {
        Page<Member> page = new PageImpl<>(List.of(member));
        when(memberRepository.findAll(any(Pageable.class))).thenReturn(page);

        memberService.getAllMembers(0, 10, "invalidField", "asc");

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(memberRepository).findAll(captor.capture());

        Pageable captured = captor.getValue();
        assertNotNull(captured.getSort().getOrderFor("firstName"));

    }
}