package com.surest.api.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserDTO {
    private UUID id;
    private String username;
    private String password;
    private UUID roleId;
    private String roleName;
}

