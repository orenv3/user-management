package com.usermanagement.requestObjects;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record CreateUserRequest(
                                @NotBlank(message = "Name is required")
                                @Size(max = 20, message = "Name must be at most 15 characters")
                                String name,
                                @NotBlank(message = "Email is required")
                                @Email(message = "Email must be a valid address (e.g. user@example.com)")
                                String email,
                                @NotNull(message = "isAdmin is required")
                                Boolean isAdmin,
                                @NotNull(message = "active is required")
                                Boolean active,
                                @NotBlank(message = "Password is required")
                                String password) {
}
