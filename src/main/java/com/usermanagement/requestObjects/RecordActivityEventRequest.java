package com.usermanagement.requestObjects;

import com.usermanagement.entities.ActivityEventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RecordActivityEventRequest(
        @NotNull(message = "Event type is required")
        ActivityEventType eventType,
        @NotBlank(message = "Path is required")
        String path,
        String sessionId,
        String action,
        String details
) {
}
