package com.usermanagement.security;

import com.usermanagement.entities.Users;
import com.usermanagement.errorHandler.UserValidationErrorException;
import com.usermanagement.mappers.EntityMapper;
import com.usermanagement.repositories.UserRepo;
import com.usermanagement.requestObjects.CreateUserRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthenticationServiceTest {

    @Mock
    private UserRepo userRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authMng;
    @Mock
    private EntityMapper entityMapper;

    private AutoCloseable mocks;
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        authenticationService = new AuthenticationService(
                userRepo, passwordEncoder, jwtService, authMng, entityMapper);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void registerUser_rejectsDuplicateEmail() {
        CreateUserRequest req = new CreateUserRequest("n", "dup@mail.com", false, true, "secret");
        Users existing = new Users(req);
        existing.setId(1L);
        when(userRepo.findByEmail("dup@mail.com")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> authenticationService.registerUser(req))
                .isInstanceOf(UserValidationErrorException.class)
                .hasMessageContaining("already exists");

        verify(userRepo, never()).save(any());
    }
}
