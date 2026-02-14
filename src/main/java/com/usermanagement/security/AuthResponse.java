package com.usermanagement.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    
    // Explicit constructor to break compilation cycle
    // public AuthResponse(String token) {
    //     this.token = token;
    // }
}
