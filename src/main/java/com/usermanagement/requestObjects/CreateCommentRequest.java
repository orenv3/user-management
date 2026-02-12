package com.usermanagement.requestObjects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Backwards-compatible alias for the original name, kept to avoid breaking external clients.
 * Prefer using {@link AdminCreateCommentRequest} in new code.
 */
public record CreateCommentRequest(
        @NotBlank @Size(max = 120) String comment,
        @NotNull Long taskId
) {
}
