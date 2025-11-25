package com.surest.api.controller;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.surest.api.dto.UserDTO;
import com.surest.api.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "APIs for managing users")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    //Create a new user
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserDTO dto) {
        log.info("Request received to create a new user with username: {}", dto.getUsername());
        UserDTO createdUser = userService.createUser(dto);
        log.info("User created successfully with ID: {}", createdUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // Retrieve all users
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        log.info("Fetching all users...");
        List<UserDTO> users = userService.getAllUsers();

        if (users == null || users.isEmpty()) {
            log.warn("No users found in the database.");
            return ResponseEntity.noContent().build();
        }

        log.info("Retrieved {} users.", users.size());
        return ResponseEntity.ok(users);
    }

    // Retrieve a specific user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable("id") UUID id) {
        log.info("Fetching user with ID: {}", id);
        UserDTO user = userService.getUserById(id);
        log.info("User retrieved: {}", user != null ? user.getId() : "Not Found");
        return ResponseEntity.ok(user);
    }

    // Update a user by ID
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateById(@PathVariable("id") UUID id, @RequestBody @Valid UserDTO dto) {
        log.info("Updating user with ID: {}", id);
        UserDTO updatedUser = userService.updateById(id, dto);
        log.info("User updated successfully: {}", id);
        return ResponseEntity.ok(updatedUser);
    }

    // Delete a user by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable("id") UUID id) {
        log.info("Request to delete user with ID: {}", id);
        userService.deleteById(id);
        log.info("User deleted successfully with ID: {}", id);
        return ResponseEntity.ok("Deleted user successfully");
    }
}
