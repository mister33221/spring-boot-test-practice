package com.kai.test_practice.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateUserRequest {

    @NotNull(message = "Name is required")
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 50, message = "Name should be between 3 and 50 characters")
    @Schema(description = "Name of the user", example = "John Doe")
    private String name;

    @NotNull(message = "Email is required")
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(description = "Email of the user", example = "johndoe@example.com")
    private String email;
}
