package com.usermanagement.responseObjects;

import java.util.Date;

/**
 * Read-only representation of a comment that is safe to expose via the API.
 */
public record CommentResponse(
        Long id,
        Date timestamp,
        String comment,
        Long userId,
        Long taskId
) {
}


