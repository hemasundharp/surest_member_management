package com.surest.api.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
	private String username;
    private String token;
    private UUID userId;
    private List<String> roles;
}
