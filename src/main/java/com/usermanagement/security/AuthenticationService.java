package com.usermanagement.security;

import com.usermanagement.utils.Role;
import com.usermanagement.entities.User;
import com.usermanagement.errorHandler.UserValidationErrorException;
import com.usermanagement.repositories.UserRepo;
import com.usermanagement.requestObjects.CreateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthenticationService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authMng;
    
    // Explicit constructor to break compilation cycle
    // public AuthenticationService(UserRepo userRepo, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authMng) {
    //     this.userRepo = userRepo;
    //     this.passwordEncoder = passwordEncoder;
    //     this.jwtService = jwtService;
    //     this.authMng = authMng;
    // }


    public AuthResponse registerUser(CreateUserRequest registerRequest) throws UserValidationErrorException {
        Optional<User> checkDuplication = userRepo.findByEmail(registerRequest.email());
        if(checkDuplication.isPresent())
            throw new UserValidationErrorException("The user: "+registerRequest.email()+" already exists");
        User user = new User(registerRequest);
        user.setRole(Role.chooseRole(user.isAdmin()));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
        return this.generateTokenByUser(user);
    }

    public AuthResponse authenticateUser(AuthenticationRequest request){
        this.authenticateUser(request.getEmail(),request.getPassword());
        return this.generateTokenByUser(userRepo.findByEmail(request.getEmail()).get());
    }

    public void authenticateUser(String email, String pass){
        authMng.authenticate(new UsernamePasswordAuthenticationToken(
                email,
                pass
        ));
    }
    private AuthResponse generateTokenByUser(User user){
        var jwtToken = jwtService.generateToken(user);
       return new AuthResponse(jwtToken);
    }

}
