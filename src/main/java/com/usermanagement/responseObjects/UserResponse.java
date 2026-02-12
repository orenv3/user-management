package com.usermanagement.responseObjects;

/**
 * Read-only representation of a user that is safe to expose via the API.
 */
public record UserResponse(
        Long id,
        String name,
        String email,
        boolean isAdmin,
        boolean active
) {
}


