package com.surest.api.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserDTO {
    private UUID id;
    private String userName;
    private String password;
    private UUID roleId;
    private String roleName; // 👈 add this line
}

