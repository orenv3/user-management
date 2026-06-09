package com.usermanagement.requestObjects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Backwards-compatible alias for the original name, kept to avoid breaking external clients.
 * Prefer using {@link AdminCreateCommentRequest} in new code.
 */
public record CreateCommentRequest(
        @NotBlank(message = "Comment is required")
        @Size(max = 120, message = "Comment must be at most 120 characters")
        String comment,
        @NotNull(message = "Task id is required")
        Long taskId
) {
}
