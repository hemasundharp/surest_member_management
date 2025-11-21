package com.surest.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleDTO {
    @NotBlank(message = "Role name is required")
    private String name;

}
