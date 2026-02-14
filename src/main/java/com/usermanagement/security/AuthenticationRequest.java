package com.usermanagement.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    private String email;
    private String password;
    
    // Explicit getters to break compilation cycle
        public String getEmail() { return email; }
        public String getPassword() { return password; }
}
