package com.surest.api.dto;

import com.surest.api.model.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
	private String username;
    private String token;
    private UUID userId;
    private Role role;
}
