package com.surest.api.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.surest.api.dto.MemberDTO;
import com.surest.api.dto.MemberPaginatedResponse;
import com.surest.api.model.Member;
import com.surest.api.service.MemberService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/members")
@Tag(name = "Member Management", description = "APIs for managing members")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j  // ✅ Enable SLF4J Logging
public class MemberController {

    private final MemberService memberService;

    // ✅ ADMIN only
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Member> createMember(@RequestBody MemberDTO dto) {
        log.info("🧾 [POST] Creating new member: {}", dto.getFirstName());
        Member member = memberService.createMember(dto);
        log.info("✅ Member created successfully with ID: {}", member.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }

    // ✅ USER & ADMIN
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<MemberPaginatedResponse> getAllMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.info("📄 [GET] Fetching all members | page={}, size={}, sortBy={}, sortDir={}",
                page, size, sortBy, sortDir);

        MemberPaginatedResponse members = memberService.getAllMembers(page, size, sortBy, sortDir);

        if (members != null && members.getData() != null) {
            log.info("✅ Retrieved {} members (totalPages={})", members.getData().size(), members.getTotalPages());
        } else {
            log.warn("⚠️ No members found or response is null");
        }
        return ResponseEntity.ok(members);
    }

    // ✅ USER & ADMIN
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable UUID id) {
        log.info("🔍 [GET] Fetching member with ID: {}", id);
        Member member = memberService.getMemberById(id);
        log.info("✅ Member found: {} {}", member.getFirstName(), member.getLastName());
        return ResponseEntity.ok(member);
    }

    // ✅ ADMIN only
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMemberById(@PathVariable UUID id, @RequestBody MemberDTO dto) {
        log.info("✏️ [PUT] Updating member with ID: {}", id);
        Member updatedMember = memberService.updateMemberById(id, dto);
        log.info("✅ Member updated successfully: {}", updatedMember.getId());
        return ResponseEntity.ok(updatedMember);
    }

    // ✅ ADMIN only
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMemberById(@PathVariable UUID id) {
        log.warn("🗑️ [DELETE] Deleting member with ID: {}", id);
        memberService.deleteMemberById(id);
        log.info("✅ Member deleted successfully with ID: {}", id);
        return ResponseEntity.ok("Member deleted successfully");
    }
}
