package com.usermanagement.security;

import com.usermanagement.config.PrivateAdminPolicy;
import com.usermanagement.dao.services.ActivityService;
import com.usermanagement.utils.Role;
import com.usermanagement.entities.Users;
import com.usermanagement.errorHandler.UserValidationErrorException;
import com.usermanagement.mappers.EntityMapper;
import com.usermanagement.repositories.UserRepo;
import com.usermanagement.requestObjects.CreateUserRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authMng;
    private final EntityMapper entityMapper;
    private final PrivateAdminPolicy privateAdminPolicy;
    private final ActivityService activityService;


    public AuthResponse registerUser(CreateUserRequest registerRequest) throws UserValidationErrorException {
        log.info("Register user requested. email={} isAdmin={}", registerRequest.email(), registerRequest.isAdmin());
        if (privateAdminPolicy.isPrivateAdmin(registerRequest.email())) {
            throw new UserValidationErrorException("This email is reserved for a protected account.");
        }
        Optional<Users> checkDuplication = userRepo.findByEmail(registerRequest.email());
        if(checkDuplication.isPresent())
            throw new UserValidationErrorException("The user: "+registerRequest.email()+" already exists");
        Users user = entityMapper.toEntity(registerRequest);
        user.setRole(Role.chooseRole(registerRequest.isAdmin()));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
        log.info("User registered. id={} email={} role={}", user.getId(), user.getEmail(), user.getRole());
        return this.generateTokenByUser(user);
    }

    /**
     * Dev-seed only. Creates users at startup including the protected private admin;
     * skips if the email already exists.
     */
    public AuthResponse registerSeedUser(CreateUserRequest registerRequest) throws UserValidationErrorException {
        log.info("Seed user requested. email={} isAdmin={}", registerRequest.email(), registerRequest.isAdmin());
        Optional<Users> existing = userRepo.findByEmail(registerRequest.email());
        if (existing.isPresent()) {
            log.info("Seed user skipped (already exists). email={}", registerRequest.email());
            return this.generateTokenByUser(existing.get());
        }
        Users user = entityMapper.toEntity(registerRequest);
        user.setRole(Role.chooseRole(registerRequest.isAdmin()));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
        log.info("Seed user created. id={} email={} role={}", user.getId(), user.getEmail(), user.getRole());
        return this.generateTokenByUser(user);
    }

    public AuthResponse authenticateUser(AuthenticationRequest request){
        log.info("Authenticate user requested. email={}", request.getEmail());
        this.authenticateUser(request.getEmail(),request.getPassword());
        Users user = userRepo.findByEmail(request.getEmail()).orElseThrow();
        log.info("Authenticate user succeeded. email={} role={}", user.getEmail(), user.getRole());
        AuthResponse response = this.generateTokenByUser(user);
        activityService.recordLogin(user.getId(), user.getEmail(), "/api/auth/login");
        return response;
    }

    public void authenticateUser(String email, String pass){
        try {
            authMng.authenticate(new UsernamePasswordAuthenticationToken(
                    email,
                    pass
            ));
        } catch (AuthenticationException ex) {
            log.warn("Authenticate user failed. email={}", email);
            throw ex;
        }
    }
    private AuthResponse generateTokenByUser(Users user){
        var jwtToken = jwtService.generateToken(user);
        String role = user.getRole() == null ? Role.chooseRole(user.isAdmin()).name() : user.getRole().name();
        return new AuthResponse(jwtToken, user.getEmail(), role, user.getId());
    }

}
