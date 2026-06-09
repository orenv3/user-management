package com.usermanagement.controllers;

import com.usermanagement.errorHandler.UserValidationErrorException;
import com.usermanagement.entities.Users;
import com.usermanagement.requestObjects.CreateUserRequest;
import com.usermanagement.repositories.UserRepo;
import com.usermanagement.security.AuthResponse;
import com.usermanagement.security.AuthenticationRequest;
import com.usermanagement.security.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@Validated
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "AuthenticationController", description = "The Authentication API. " +
        "Responsible on user authentications and adding new users to the system(create users).")
@RequestMapping("/api/auth/")
@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserRepo userRepo;


    @PostMapping("admin/registerUser")
    @Operation(summary = "Register a new user (admin only)")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody CreateUserRequest registerRequest) throws UserValidationErrorException {
        return ResponseEntity.ok(authenticationService.registerUser(registerRequest));
    }

    @PostMapping("login")
    @Operation(summary = "Login and receive a JWT")
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody AuthenticationRequest authRequest){
        return ResponseEntity.ok(authenticationService.authenticateUser(authRequest));
    }

    @GetMapping("me")
    @Operation(summary = "Get current authenticated user info")
    public ResponseEntity<AuthResponse> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth == null ? null : auth.getName();
        if (email == null || email.isBlank()) {
            return ResponseEntity.status(401).build();
        }
        Users user = userRepo.findByEmail(email).orElseThrow();
        String role = user.getRole() == null ? com.usermanagement.utils.Role.chooseRole(user.isAdmin()).name() : user.getRole().name();
        return ResponseEntity.ok(new AuthResponse(null, user.getEmail(), role, user.getId()));
    }
}
