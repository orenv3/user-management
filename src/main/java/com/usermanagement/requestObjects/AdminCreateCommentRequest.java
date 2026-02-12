package com.usermanagement.requestObjects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Admin command: add a comment to any task (user is derived from the task's assignee).
 */
public record AdminCreateCommentRequest(
        @NotBlank @Size(max = 120) String comment,
        @NotNull Long taskId
) {
}
