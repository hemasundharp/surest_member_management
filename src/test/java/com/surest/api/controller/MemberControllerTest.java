package com.surest.api.controller;

import com.surest.api.dto.MemberDTO;
import com.surest.api.dto.MemberPaginatedResponse;
import com.surest.api.exception.UserNotFoundException;
import com.surest.api.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberControllerTest {

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;
    private MemberDTO memberDTO;
    private UUID memberId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        memberId = UUID.randomUUID();
        memberDTO = new MemberDTO();
        memberDTO.setId(memberId);
        memberDTO.setFirstName("John");
        memberDTO.setLastName("Doe");
        memberDTO.setEmail("john@example.com");
        memberDTO.setDateOfBirth(LocalDate.of(1990, 1, 1));
    }

    @Test
    void createMember_success() {
        when(memberService.createMember(memberDTO)).thenReturn(memberDTO);
        ResponseEntity<MemberDTO> response = memberController.createMember(memberDTO);
        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(memberDTO, response.getBody());
        verify(memberService).createMember(memberDTO);
    }

    @Test
    void getAllMembers_success() {
        MemberPaginatedResponse paginatedResponse = new MemberPaginatedResponse(
                List.of(memberDTO), 1, 1, 0
        );
        when(memberService.getAllMembers(0, 10, "firstName", "asc"))
                .thenReturn(paginatedResponse);
        ResponseEntity<MemberPaginatedResponse> response =
                memberController.getAllMembers(0, 10, "firstName", "asc");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().getData().size());
        verify(memberService).getAllMembers(0, 10, "firstName", "asc");
    }

    @Test
    void getMemberById_success() {
        when(memberService.getMemberById(memberId)).thenReturn(memberDTO);
        ResponseEntity<MemberDTO> response = memberController.getMemberById(memberId);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(memberDTO, response.getBody());
        verify(memberService).getMemberById(memberId);
    }

    @Test
    void updateMemberById_success() {
        when(memberService.updateMemberById(memberId, memberDTO)).thenReturn(memberDTO);
        ResponseEntity<MemberDTO> response = memberController.updateMemberById(memberId, memberDTO);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(memberDTO, response.getBody());
        verify(memberService).updateMemberById(memberId, memberDTO);
    }

    @Test
    void deleteMemberById_success() {
        doNothing().when(memberService).deleteMemberById(memberId);
        ResponseEntity<String> response = memberController.deleteMemberById(memberId);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Member deleted successfully", response.getBody());
        verify(memberService).deleteMemberById(memberId);
    }

    @Test
    void getMemberById_notFound() {
        when(memberService.getMemberById(memberId)).thenThrow(new UserNotFoundException("Member not found"));
        assertThrows(UserNotFoundException.class, () -> memberController.getMemberById(memberId));
    }

    @Test
    void updateMemberById_notFound() {
        when(memberService.updateMemberById(memberId, memberDTO))
                .thenThrow(new UserNotFoundException("Member not found"));

        assertThrows(UserNotFoundException.class, () -> memberController.updateMemberById(memberId, memberDTO));
    }

    @Test
    void deleteMemberById_notFound() {
        doThrow(new UserNotFoundException("Member not found")).when(memberService).deleteMemberById(memberId);
        assertThrows(UserNotFoundException.class, () -> memberController.deleteMemberById(memberId));
    }

    @Test
    void getAllMembers_serviceThrowsException() {
        when(memberService.getAllMembers(0, 10, "firstName", "asc"))
                .thenThrow(new RuntimeException("DB error"));
        assertThrows(RuntimeException.class,
                () -> memberController.getAllMembers(0, 10, "firstName", "asc"));
    }

    @Test
    void createMember_serviceThrowsException() {
        when(memberService.createMember(memberDTO)).thenThrow(new RuntimeException("DB error"));
        assertThrows(RuntimeException.class,
                () -> memberController.createMember(memberDTO));
    }

    @Test
    void getAllMembers_membersNull_logsWarning() {
        when(memberService.getAllMembers(0, 10, "firstName", "asc")).thenReturn(null);
        ResponseEntity<MemberPaginatedResponse> response =
                memberController.getAllMembers(0, 10, "firstName", "asc");
        assertNull(response.getBody());
    }

    @Test
    void getAllMembers_membersDataNull_logsWarning() {
        MemberPaginatedResponse emptyResponse = new MemberPaginatedResponse(null, 0, 0, 0);
        when(memberService.getAllMembers(0, 10, "firstName", "asc")).thenReturn(emptyResponse);
        ResponseEntity<MemberPaginatedResponse> response =
                memberController.getAllMembers(0, 10, "firstName", "asc");
        assertNull(response.getBody().getData());
    }
}
