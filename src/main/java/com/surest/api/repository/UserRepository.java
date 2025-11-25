package com.surest.api.repository;

import com.surest.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    //  Find user by username (used in authentication)
    Optional<User> findByUsername(String username);

    //  Check if a username already exists (useful for registration)
    boolean existsByUsername(String username);
}
