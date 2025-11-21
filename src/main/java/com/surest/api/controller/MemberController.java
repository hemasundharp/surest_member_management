package com.surest.api.controller;

import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.surest.api.dto.MemberDTO;
import com.surest.api.dto.MemberPaginatedResponse;
import com.surest.api.service.MemberService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/members")
@Tag(name = "Member Management", description = "APIs for managing members")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<MemberDTO> createMember(@RequestBody @Valid MemberDTO dto) {
        log.info("POST Creating new member: {}", dto.getFirstName());
        MemberDTO member = memberService.createMember(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<MemberPaginatedResponse> getAllMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        MemberPaginatedResponse members = memberService.getAllMembers(page, size, sortBy, sortDir);
        return ResponseEntity.ok(members);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<MemberDTO> getMemberById(@PathVariable UUID id) {
        MemberDTO member = memberService.getMemberById(id);
        return ResponseEntity.ok(member);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MemberDTO> updateMemberById(@PathVariable UUID id, @RequestBody @Valid MemberDTO dto) {
        MemberDTO updatedMember = memberService.updateMemberById(id, dto);
        return ResponseEntity.ok(updatedMember);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMemberById(@PathVariable UUID id) {
        memberService.deleteMemberById(id);
        return ResponseEntity.ok("Member deleted successfully");
    }
}
