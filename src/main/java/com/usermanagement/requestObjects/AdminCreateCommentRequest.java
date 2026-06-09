package com.usermanagement.requestObjects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Admin command: add a comment to any task (user is derived from the task's assignee).
 */
public record AdminCreateCommentRequest(
        @NotBlank(message = "Comment is required")
        @Size(max = 120, message = "Comment must be at most 120 characters")
        String comment,
        @NotNull(message = "Task id is required")
        Long taskId
) {
}
