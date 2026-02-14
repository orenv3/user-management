package com.usermanagement.mappers;

import com.usermanagement.entities.Comment;
import com.usermanagement.entities.Task;
import com.usermanagement.entities.User;
import com.usermanagement.responseObjects.CommentResponse;
import com.usermanagement.responseObjects.TaskResponse;
import com.usermanagement.responseObjects.UserResponse;
import org.springframework.stereotype.Component;

/**
 * Centralized mapper utility for converting entities to DTOs.
 * This ensures entities are never exposed directly through the API.
 */
@Component
public class DtoMapper {

    /**
     * Maps a User entity to UserResponse DTO.
     * Excludes sensitive information like password.
     *
     * @param user the User entity to map
     * @return UserResponse DTO, or null if user is null
     */
    public UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.isAdmin(),
                user.isActive()
        );
    }

    /**
     * Maps a Task entity to TaskResponse DTO.
     * Flattens the assignee relationship to just the assignee ID.
     *
     * @param task the Task entity to map
     * @return TaskResponse DTO, or null if task is null
     */
    public TaskResponse toTaskResponse(Task task) {
        if (task == null) {
            return null;
        }
        Long assigneeId = task.getAssignee() != null ? task.getAssignee().getId() : null;
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                assigneeId
        );
    }

    /**
     * Maps a Comment entity to CommentResponse DTO.
     * Flattens the user and task relationships to just their IDs.
     *
     * @param comment the Comment entity to map
     * @return CommentResponse DTO, or null if comment is null
     */
    public CommentResponse toCommentResponse(Comment comment) {
        if (comment == null) {
            return null;
        }
        Long userId = comment.getUserId() != null ? comment.getUserId().getId() : null;
        Long taskId = comment.getTaskId() != null ? comment.getTaskId().getId() : null;
        return new CommentResponse(
                comment.getId(),
                comment.getTimestamp(),
                comment.getComment(),
                userId,
                taskId
        );
    }
}

