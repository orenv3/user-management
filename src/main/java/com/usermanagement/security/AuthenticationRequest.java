package com.usermanagement.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid address (e.g. user@example.com)")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    public String getEmail() { return email; }
    public String getPassword() { return password; }
}
