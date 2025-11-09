package com.surest.api.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class MemberDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String email;
}
