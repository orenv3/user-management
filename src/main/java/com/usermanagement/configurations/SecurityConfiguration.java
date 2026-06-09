package com.usermanagement.configurations;

import com.usermanagement.security.AuthFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfiguration.class);

    private final AuthFilter authFilter;
    private final AuthenticationProvider authProvider;

    @Bean // SecurityFilterChain is responsible/config for all the traffic and filters of http of our APP
    public SecurityFilterChain getSecurityFilterChain(HttpSecurity http) throws Exception {

        http.csrf()
                .disable()
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) -> {
                            log.warn(
                                    "Unauthorized request (401). method={} uri={} message={}",
                                    request.getMethod(),
                                    request.getRequestURI(),
                                    safeMessage(authException)
                            );
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            log.warn(
                                    "Access denied (403). method={} uri={} message={}",
                                    request.getMethod(),
                                    request.getRequestURI(),
                                    safeMessage(accessDeniedException)
                            );
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                        })
                )
                .authorizeHttpRequests()
                .requestMatchers(req->  req.getRequestURI().contains("swagger-ui"))
                .permitAll()
                .requestMatchers(req->  req.getRequestURI().contains("api-docs"))
                .permitAll()
                .requestMatchers(req -> {
                    String uri = req.getRequestURI();
                    return uri.equals("/")
                            || uri.equals("/login")
                            || uri.equals("/index.html")
                            || uri.startsWith("/assets/")
                            || uri.equals("/favicon.ico");
                }).permitAll()
                .requestMatchers(req->  req.getRequestURI().contains("/auth/login"))
                .permitAll()
                .requestMatchers(req-> req.getRequestURI().equals("/api/analytics/event"))
                .permitAll()
                .requestMatchers(req->  req.getRequestURI().contains("/user/")).hasAuthority("USER")
                .requestMatchers(req-> req.getRequestURI().contains("/admin/")).hasAuthority("ADMIN")
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                //AuthenticationProvider --> Data access object which responsible to fetch user details/encode password etc.
                // an Authentication request is processed by an AuthenticationProvider,
                .authenticationProvider(authProvider)
                //UsernamePasswordAuthenticationFilter  by default responds to the URL /login. Therefor authFilter need to be first
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    private static String safeMessage(Exception ex) {
        String msg = ex.getMessage();
        if (msg == null || msg.isBlank()) {
            return ex.getClass().getSimpleName();
        }
        return msg;
    }
}
