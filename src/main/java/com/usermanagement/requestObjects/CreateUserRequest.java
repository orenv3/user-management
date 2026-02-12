package com.usermanagement.requestObjects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record CreateUserRequest(
                                @NotBlank @Size(max=15) String name,
                                @NotBlank String email,
                                @NotNull boolean isAdmin,
                                @NotNull boolean active,
                                @NotBlank String password) {
}
