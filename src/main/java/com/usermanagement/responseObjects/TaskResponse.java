package com.usermanagement.responseObjects;

/**
 * Read-only representation of a task that is safe to expose via the API.
 */
public record TaskResponse(
        Long id,
        String title,
        String description,
        String status,
        Long assigneeId
) {
}


