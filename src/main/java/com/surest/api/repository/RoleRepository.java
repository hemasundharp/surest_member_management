package com.surest.api.repository;

import com.surest.api.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    // Find a role by its name (e.g. "ADMIN", "USER")
    Optional<Role> findByName(String name);

}
