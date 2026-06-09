package com.usermanagement.responseObjects;

import com.usermanagement.entities.ActivityEvent;
import com.usermanagement.entities.ActivityEventType;

import java.time.Instant;
import java.util.List;

public record ActivityEventResponse(
        Long id,
        Instant timestamp,
        ActivityEventType eventType,
        Long userId,
        String email,
        String sessionId,
        String path,
        String action,
        String details
) {
    public static ActivityEventResponse from(ActivityEvent event) {
        return new ActivityEventResponse(
                event.getId(),
                event.getTimestamp(),
                event.getEventType(),
                event.getUserId(),
                event.getEmail(),
                event.getSessionId(),
                event.getPath(),
                event.getAction(),
                event.getDetails()
        );
    }
}
