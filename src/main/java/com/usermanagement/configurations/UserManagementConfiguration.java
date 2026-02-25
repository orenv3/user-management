package com.usermanagement.configurations;

import com.usermanagement.utils.Role;
import com.usermanagement.entities.User;
import com.usermanagement.errorHandler.GeneralExceptionHandler;
import com.usermanagement.repositories.UserRepo;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ComponentScan(basePackages = {"com.usermanagement"})
@Aspect
@AllArgsConstructor
@EntityScan(basePackages = "com.usermanagement.entities")
@EnableJpaRepositories(basePackages = "com.usermanagement.repositories")
@Import(GeneralExceptionHandler.class)
@Configuration
public class UserManagementConfiguration {


private final UserRepo userRepo;


    @Bean
    public UserDetailsService getUserDetailsService(){
        UserDetailsService userDetailsService = new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                Optional<User> user =  userRepo.findByEmail(username);
                User curr=null;
                if(user.isPresent()){
                     curr = user.get();
                    curr.setRole(Role.chooseRole(curr.isAdmin()));
                }else{
                    throw new UsernameNotFoundException("The user: "+username+" not found");
                }

                return curr;
            }
        };
        return userDetailsService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(getUserDetailsService());// set how to fetch user details / with which service etc
        provider.setPasswordEncoder(passwordEncoder()); // set in which password encoder we want to use to encode the password
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration conf) throws Exception {
        return conf.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
       return  new BCryptPasswordEncoder();
    }

    @AfterReturning(value = "execution(* com.usermanagement.controllers..*.*(..))", returning = "returningObject")
    public void afterEachPrintOutDbObject(JoinPoint join, Object returningObject){
        System.out.println("Object from DB: " + returningObject);
    }

    @Bean
    public OpenAPI customizeOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                        .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
