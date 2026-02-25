package com.usermanagement.requestObjects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * User comment: a specific user adds a comment on a specific task.
 */
public record UserTaskCommentRequest(
        @NotBlank @Size(max = 120) String comment,
        @NotNull Long taskId,
        @NotNull Long userId
) {
}


