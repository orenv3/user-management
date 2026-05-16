package com.usermanagement.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class AuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationProvider authenticationProvider;
    



    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String requestUri = request.getRequestURI();
        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        final String userEmail;

        if (authHeader == null || !(authHeader.startsWith("Bearer "))) {
            log.debug("No bearer token provided. method={} uri={}", request.getMethod(), requestUri);
            filterChain.doFilter(request, response);
            return;
        }
        jwtToken = authHeader.substring(7);
        try {
            userEmail = jwtService.extractUserEmail(jwtToken);
        } catch (RuntimeException ex) {
            log.warn("Failed extracting userEmail from JWT. method={} uri={}", request.getMethod(), requestUri, ex);
            filterChain.doFilter(request, response);
            return;
        }

        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
        if (userEmail != null && existingAuth == null) {
            final UserDetails userDetails;
            try {
                // checking userName via DB
                userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            } catch (UsernameNotFoundException ex) {
                log.warn("JWT subject not found in DB. method={} uri={} subject={}", request.getMethod(), requestUri, userEmail);
                filterChain.doFilter(request, response);
                return;
            }

            if (jwtService.isTokenValid(jwtToken, userDetails)) {
                //UsernamePasswordAuthenticationToken is required to update SecurityContext
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info(
                        "Authenticated request via JWT. method={} uri={} subject={} authorities={}",
                        request.getMethod(),
                        requestUri,
                        userEmail,
                        userDetails.getAuthorities()
                );

            } else {
                log.warn(
                        "JWT token invalid for subject. method={} uri={} subject={}",
                        request.getMethod(),
                        requestUri,
                        userEmail
                );
            }
        } else if (userEmail == null) {
            log.warn("JWT extracted subject was null. method={} uri={}", request.getMethod(), requestUri);
        } else {
            log.debug(
                    "SecurityContext already has authentication; skipping JWT auth. method={} uri={} existingAuthType={}",
                    request.getMethod(),
                    requestUri,
                    existingAuth.getClass().getSimpleName()
            );
        }
        filterChain.doFilter(request, response);
    }
}
