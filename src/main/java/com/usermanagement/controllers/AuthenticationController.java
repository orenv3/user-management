package com.usermanagement.controllers;

import com.usermanagement.errorHandler.UserValidationErrorException;
import com.usermanagement.requestObjects.CreateUserRequest;
import com.usermanagement.security.AuthResponse;
import com.usermanagement.security.AuthenticationRequest;
import com.usermanagement.security.AuthenticationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@Validated
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "AuthenticationController", description = "The Authentication API. " +
        "Responsible on user authentications and adding new users to the system(create users).")
@RequestMapping("/api/auth/")
@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;


    @PostMapping("admin/registerUser")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody CreateUserRequest registerRequest){
        try {
            return ResponseEntity.ok(authenticationService.registerUser(registerRequest));
        } catch (UserValidationErrorException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthenticationRequest authRequest){
        return ResponseEntity.ok(authenticationService.authenticateUser(authRequest));
    }
}
