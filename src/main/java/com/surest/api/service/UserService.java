package com.surest.api.service;

import java.util.List;
import java.util.UUID;

import com.surest.api.dto.AuthenticationResponse;
import com.surest.api.dto.SignIn;
import com.surest.api.dto.UserDTO;

public interface UserService {

    AuthenticationResponse authenticateUser(SignIn loginDto);

    UserDTO createUser(UserDTO dto);

    List<UserDTO> getAllUsers();

    UserDTO getUserById(UUID id);

    UserDTO updateById(UUID id, UserDTO dto);

    void deleteById(UUID id);
}
