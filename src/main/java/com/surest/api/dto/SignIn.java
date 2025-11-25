package com.surest.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignIn {
	   @NotNull @NotEmpty @NotBlank
	    private String username;
	    @NotNull @NotEmpty @NotBlank
	    private String password;

}
