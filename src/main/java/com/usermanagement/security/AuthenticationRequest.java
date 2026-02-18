package com.usermanagement.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationRequest {
    private final String email;
    private final String password;
    
    // Explicit getters to break compilation cycle
        public String getEmail() { return email; }
        public String getPassword() { return password; }
}
