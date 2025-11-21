package com.surest.api.service.impl;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.surest.api.model.Role;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.surest.api.config.JwtTokenUtil;
import com.surest.api.dto.AuthenticationResponse;
import com.surest.api.dto.SignIn;
import com.surest.api.dto.UserDTO;
import com.surest.api.exception.InvalidLoginException;
import com.surest.api.exception.UserNotFoundException;
import com.surest.api.mapper.UserMapper;
import com.surest.api.model.User;
import com.surest.api.repository.RoleRepository;
import com.surest.api.repository.UserRepository;
import com.surest.api.service.AuthenticationService;
import com.surest.api.service.UserService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtTokenUtil jwtUtil;
    private final AuthenticationService authenticationService;

    @Override
    @Transactional(readOnly = true)
    public AuthenticationResponse authenticateUser(SignIn loginDto) {
        log.info("Attempting authentication for user: {}", loginDto.getUsername());
        try {
            Authentication authentication = authenticationService.authenticateWithCredentials(
                    loginDto.getUsername(), loginDto.getPassword());

            User user = (User) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(user);

            log.info("Authentication successful for user: {}", user.getUsername());
            return new AuthenticationResponse(
                    user.getUsername(),
                    accessToken,
                    user.getId(),
                    user.getRoles().stream()
                            .map(role -> role.getName().toUpperCase())
                            .collect(Collectors.toList())
            );
        } catch (BadCredentialsException e) {
            log.warn("Authentication failed for username: {}", loginDto.getUsername());
            throw new InvalidLoginException("Invalid username or password!");
        }
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public UserDTO createUser(UserDTO dto) {
        log.info("Creating user: {}", dto.getUsername());

        User user = userMapper.toEntity(dto);


        Set<Role> roles = dto.getRoleId().stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId)))
                .collect(Collectors.toSet());

        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());
        return userMapper.toDto(savedUser);
    }

    @Override
    @Cacheable(value = "users")
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        log.info("Fetching all users (from DB or cache)");
        List<UserDTO> users = userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
        log.debug("Fetched {} users", users.size());
        return users;
    }

    @Override
    @Cacheable(value = "users", key = "#id")
    @Transactional(readOnly = true)
    public UserDTO getUserById(UUID id) {
        log.info("Fetching user by ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));
        return userMapper.toDto(user);
    }

    @Override
    @CacheEvict(value = "users", key = "#id")
    @Transactional
    public UserDTO updateById(UUID id, UserDTO dto) {
        log.info("Updating user with ID: {}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));

        existingUser.setUsername(dto.getUsername());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getRoleId() != null && !dto.getRoleId().isEmpty()) {
            Set<Role> roles = dto.getRoleId().stream()
                    .map(roleId -> roleRepository.findById(roleId)
                            .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId)))
                    .collect(Collectors.toSet());
            existingUser.setRoles(roles);
        }

        User updatedUser = userRepository.save(existingUser);
        log.info("User updated successfully: {}", updatedUser.getId());
        return userMapper.toDto(updatedUser);
    }

    @Override
    @CacheEvict(value = "users", key = "#id")
    @Transactional
    public void deleteById(UUID id) {
        log.warn("Deleting user with ID: {}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));

        userRepository.delete(existingUser);

        log.info("User deleted successfully");
    }
}
