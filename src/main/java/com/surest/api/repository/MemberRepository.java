package com.surest.api.repository;

import com.surest.api.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID>, JpaSpecificationExecutor<Member> {

    //find member by email (common lookup)
    Optional<Member> findByEmail(String email);

    // check if email exists (useful for validation)
    boolean existsByEmail(String email);
}
