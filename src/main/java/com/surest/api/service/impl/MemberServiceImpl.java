package com.surest.api.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.surest.api.exception.BusinessServiceException;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.surest.api.dto.MemberDTO;
import com.surest.api.dto.MemberPaginatedResponse;
import com.surest.api.exception.UserNotFoundException;
import com.surest.api.mapper.MemberMapper;
import com.surest.api.model.Member;
import com.surest.api.repository.MemberRepository;
import com.surest.api.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public MemberDTO createMember(MemberDTO dto) {
        log.info("Creating new member: {} {}", dto.getFirstName(), dto.getLastName());
        if (memberRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessServiceException("Email already exists", HttpStatus.CONFLICT);
        }

        Member member = memberMapper.toEntity(dto);
        member.setCreatedAt(LocalDateTime.now());
        member.setUpdatedAt(LocalDateTime.now());
        Member saved = memberRepository.save(member);
        log.info("Member created with ID: {}", saved.getId());
        return memberMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberPaginatedResponse getAllMembers(int page, int size, String sortBy, String sortDir) {
        log.debug("Fetching members list: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);

        String sortField = (sortBy != null && isSortableField(sortBy)) ? sortBy : "firstName";
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<Member> memberPage = memberRepository.findAll(pageable);

        List<MemberDTO> memberDTOs = memberPage.getContent().stream()
                .map(memberMapper::toDto)
                .collect(Collectors.toList());

        log.info("Retrieved {} members (page {} of {})",
                memberPage.getNumberOfElements(), memberPage.getNumber() + 1, memberPage.getTotalPages());

        return new MemberPaginatedResponse(
                memberDTOs,
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
    @Transactional(readOnly = true)
    public MemberDTO getMemberById(UUID id) {
        log.info("Fetching member by ID: {} (cacheable)", id);
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Member not found with ID: " + id));
        return memberMapper.toDto(member);
    }

    @Override
    @CacheEvict(value = "members", key = "#id")
    @Transactional
    public MemberDTO updateMemberById(UUID id, MemberDTO dto) {
        log.info("Updating member with ID: {}", id);
        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Member not found with ID: " + id));

        BeanUtils.copyProperties(dto, existingMember, "id", "createdAt", "updatedAt");
        existingMember.setUpdatedAt(LocalDateTime.now());
        Member updated = memberRepository.save(existingMember);
        log.info("Member updated successfully: {}", id);
        return memberMapper.toDto(updated);
    }

    @Override
    @CacheEvict(value = "members", key = "#id")
    @Transactional
    public void deleteMemberById(UUID id) {
        log.warn("Deleting member with ID: {}", id);
        if (!memberRepository.existsById(id)) {
            throw new BusinessServiceException("Member not found", HttpStatus.NOT_FOUND);
        }
        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Member not found with ID: " + id));
        memberRepository.delete(existingMember);
        log.info("Member deleted and cache evicted: {}", id);
    }
}
