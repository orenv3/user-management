package com.usermanagement.config;

import com.usermanagement.dao.services.ActivityService;
import com.usermanagement.repositories.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class ActivityLoggingAspect {

    private final ActivityService activityService;
    private final UserRepo userRepo;

    @AfterReturning("@within(org.springframework.web.bind.annotation.RestController) && within(com.usermanagement.controllers..*)")
    public void logControllerAction(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        if (className.endsWith("AnalyticsController") || className.endsWith("AuthenticationController")) {
            return;
        }

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return;
        }
        HttpServletRequest request = attrs.getRequest();
        String path = request.getRequestURI();
        if (path.startsWith("/api/analytics/") || path.contains("/auth/login")) {
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return;
        }

        String email = auth.getName();
        Long userId = userRepo.findByEmail(email).map(u -> u.getId()).orElse(null);
        String action = joinPoint.getSignature().getName();
        activityService.recordAction(path, action, userId, email, request.getMethod());
    }
}
