package com.surest.api.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.surest.api.dto.MemberDTO;
import com.surest.api.dto.MemberPaginatedResponse;
import com.surest.api.exception.UserNotFoundException;
import com.surest.api.mapper.MemberMapper;
import com.surest.api.model.Member;
import com.surest.api.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService implements com.surest.api.service.MemberService {

    private final MemberMapper memberMapper;
    private final MemberRepository memberRepository;

    @Override
    public Member createMember(MemberDTO dto) {
        log.info("Creating new member: {} {}", dto.getFirstName(), dto.getLastName());
        Member member = memberMapper.toEntity(dto);
        member.setCreatedAt(LocalDateTime.now());
        member.setUpdatedAt(LocalDateTime.now());
        Member saved = memberRepository.save(member);
        log.info("Member created with ID: {}", saved.getId());
        return saved;
    }

    @Override
    public MemberPaginatedResponse getAllMembers(int page, int size, String sortBy, String sortDir) {
        log.debug("Fetching members list: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);

        String sortField = (sortBy != null && isSortableField(sortBy)) ? sortBy : "firstName";
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<Member> memberPage = memberRepository.findAll(pageable);

        log.info("Retrieved {} members (page {} of {})",
                memberPage.getNumberOfElements(), memberPage.getNumber() + 1, memberPage.getTotalPages());

        return new MemberPaginatedResponse(
                memberPage.getContent(),
                memberPage.getTotalElements(),
                memberPage.getTotalPages(),
                memberPage.getNumber()
        );
    }

    private boolean isSortableField(String field) {
        return switch (field) {
            case "firstName", "lastName", "email", "dateOfBirth", "createdAt" -> true;
            default -> false;
        };
    }

    @Override
    @Cacheable(value = "members", key = "#id")
    public Member getMemberById(UUID id) {
        log.info("Fetching member by ID: {} (cacheable)", id);
        return memberRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Member not found with ID: " + id));
    }

    @Override
    @CacheEvict(value = "members", key = "#id")
    public Member updateMemberById(UUID id, MemberDTO dto) {
        log.info("Updating member with ID: {}", id);
        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Member not found with ID: " + id));

        BeanUtils.copyProperties(dto, existingMember, "memberId", "createdAt");
        existingMember.setUpdatedAt(LocalDateTime.now());
        Member updated = memberRepository.save(existingMember);
        log.info("Member updated successfully: {}", id);
        return updated;
    }

    @Override
    @CacheEvict(value = "members", key = "#id")
    public void deleteMemberById(UUID id) {
        log.warn("Deleting member with ID: {}", id);
        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Member not found with ID: " + id));
        memberRepository.delete(existingMember);
        log.info("Member deleted and cache evicted: {}", id);
    }
}
