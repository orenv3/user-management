package com.usermanagement.security;

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


    public AuthResponse registerUser(CreateUserRequest registerRequest) throws UserValidationErrorException {
        log.info("Register user requested. email={} isAdmin={}", registerRequest.email(), registerRequest.isAdmin());
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

    public AuthResponse authenticateUser(AuthenticationRequest request){
        log.info("Authenticate user requested. email={}", request.getEmail());
        this.authenticateUser(request.getEmail(),request.getPassword());
        Users user = userRepo.findByEmail(request.getEmail()).orElseThrow();
        log.info("Authenticate user succeeded. email={} role={}", user.getEmail(), user.getRole());
        return this.generateTokenByUser(user);
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
