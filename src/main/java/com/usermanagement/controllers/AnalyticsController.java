package com.usermanagement.controllers;

import com.usermanagement.dao.services.ActivityService;
import com.usermanagement.entities.ActivityEventType;
import com.usermanagement.entities.Users;
import com.usermanagement.repositories.UserRepo;
import com.usermanagement.requestObjects.RecordActivityEventRequest;
import com.usermanagement.responseObjects.ActivityEventResponse;
import com.usermanagement.responseObjects.ActivitySummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@Validated
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "AnalyticsController", description = "Activity tracking and analytics.")
@RequestMapping("/api/analytics/")
@RestController
public class AnalyticsController {

    private final ActivityService activityService;
    private final UserRepo userRepo;

    @PostMapping("event")
    @Operation(summary = "Record a client-side activity event (page view)")
    public ResponseEntity<ActivityEventResponse> recordEvent(@Valid @RequestBody RecordActivityEventRequest request) {
        AuthContext auth = resolveAuth();
        return ResponseEntity.ok(activityService.recordEvent(request, auth.userId(), auth.email()));
    }

    @GetMapping("admin/summary")
    @Operation(summary = "Activity summary counts (admin only)")
    public ActivitySummaryResponse summary() {
        return activityService.getSummary();
    }

    @GetMapping("admin/events")
    @Operation(summary = "Paginated activity event log (admin only)")
    public Page<ActivityEventResponse> events(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) ActivityEventType eventType
    ) {
        return activityService.getEvents(pageNumber, pageSize, eventType);
    }

    private AuthContext resolveAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return new AuthContext(null, null);
        }
        String email = auth.getName();
        return userRepo.findByEmail(email)
                .map(u -> new AuthContext(u.getId(), u.getEmail()))
                .orElse(new AuthContext(null, email));
    }

    private record AuthContext(Long userId, String email) {
    }
}
