package com.usermanagement.security;

import com.usermanagement.entities.Users;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    // Base64 (matches test/resources config value)
    private static final String SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    @Test
    void generateToken_andExtractSubject_roundTrip() {
        JwtService jwtService = new JwtService(SECRET);
        Users u = new Users();
        u.setEmail("user@example.com");
        u.setActive(true);
        u.setAdmin(false);

        String token = jwtService.generateToken(u);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUserEmail(token)).isEqualTo("user@example.com");
        assertThat(jwtService.isTokenValid(token, u)).isTrue();
    }

    @Test
    void tokenSignedWithDifferentSecret_isRejected() {
        JwtService signer = new JwtService(SECRET);
        JwtService verifierWithDifferentSecret = new JwtService("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=");

        Users u = new Users();
        u.setEmail("user@example.com");
        u.setActive(true);
        u.setAdmin(false);

        String token = signer.generateToken(u);

        assertThatThrownBy(() -> verifierWithDifferentSecret.extractUserEmail(token))
                .isInstanceOf(RuntimeException.class);
    }
}

