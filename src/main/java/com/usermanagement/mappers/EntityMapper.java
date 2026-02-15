package com.usermanagement.mappers;

import com.usermanagement.entities.Comment;
import com.usermanagement.entities.Task;
import com.usermanagement.entities.User;
import com.usermanagement.requestObjects.*;
import com.usermanagement.responseObjects.CommentResponse;
import com.usermanagement.responseObjects.CommentsResponse;
import com.usermanagement.responseObjects.TaskResponse;
import com.usermanagement.responseObjects.TaskTableResponse;
import com.usermanagement.responseObjects.UserResponse;
import org.mapstruct.*;

/**
 * Centralized MapStruct mapper for converting between DTOs and entities.
 * This ensures entities are never exposed directly through the API.
 * 
 * MapStruct generates implementation at compile time for optimal performance.
 * All mappings handle null safety and follow best practices.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EntityMapper {

    // ==================== REQUEST DTOs TO ENTITIES ====================

    /**
     * Maps CreateUserRequest to User entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toEntity(CreateUserRequest request);

    /**
     * Maps CreateTaskRequest to Task entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    Task toEntity(CreateTaskRequest request);

    /**
     * Maps AdminCreateCommentRequest to Comment entity.
     * Note: taskId and userId must be set separately in service layer.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "taskId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "timestamp", expression = "java(new java.util.Date())")
    Comment toEntity(AdminCreateCommentRequest request);

    /**
     * Maps UserTaskCommentRequest to Comment entity.
     * Note: taskId and userId must be set separately in service layer.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "taskId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "timestamp", expression = "java(new java.util.Date())")
    Comment toEntity(UserTaskCommentRequest request);

    // ==================== UPDATE DTOs TO ENTITIES ====================

    /**
     * Updates User entity with values from UpdateUserRequest.
     * Only non-null fields are updated (partial update).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "name", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "email", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "password", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromRequest(UpdateUserRequest request, @MappingTarget User user);

    /**
     * Updates Task entity with values from UpdateTaskRequest.
     * Only non-null fields are updated (partial update).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "title", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "description", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "status", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTaskFromRequest(UpdateTaskRequest request, @MappingTarget Task task);

    /**
     * Updates Comment entity with values from UpdateCommentRequest.
     * Only non-null fields are updated (partial update).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "taskId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "timestamp", expression = "java(new java.util.Date())")
    @Mapping(target = "comment", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCommentFromRequest(UpdateCommentRequest request, @MappingTarget Comment comment);

    // ==================== ENTITIES TO RESPONSE DTOs ====================

    /**
     * Maps User entity to UserResponse DTO.
     * Excludes sensitive information like password.
     */
    UserResponse toUserResponse(User user);

    /**
     * Maps Task entity to TaskResponse DTO.
     * Flattens the assignee relationship to just the assignee ID.
     */
    @Mapping(target = "assigneeId", source = "assignee.id")
    TaskResponse toTaskResponse(Task task);

    /**
     * Maps Comment entity to CommentResponse DTO.
     * Flattens the user and task relationships to just their IDs.
     */
    @Mapping(target = "userId", source = "userId.id")
    @Mapping(target = "taskId", source = "taskId.id")
    CommentResponse toCommentResponse(Comment comment);

    /**
     * Maps Comment entity to CommentsResponse DTO.
     * Includes task title. Error message defaults to empty string.
     */
    @Mapping(target = "userId", source = "userId.id")
    @Mapping(target = "taskId", source = "taskId.id")
    @Mapping(target = "title", source = "taskId.title")
    @Mapping(target = "err", constant = "")
    CommentsResponse toCommentsResponse(Comment comment);

    /**
     * Maps Comment entity to CommentsResponse DTO with custom error message.
     */
    @Mapping(target = "userId", source = "userId.id")
    @Mapping(target = "taskId", source = "taskId.id")
    @Mapping(target = "title", source = "taskId.title")
    @Mapping(target = "err", source = "errorMessage")
    CommentsResponse toCommentsResponseWithError(Comment comment, String errorMessage);

    /**
     * Maps Task entity to TaskTableResponse DTO.
     * Uses snake_case field names as per the record definition.
     * Error message defaults to empty string.
     */
    @Mapping(target = "task_id", source = "id")
    @Mapping(target = "task_status", source = "status")
    @Mapping(target = "task_assignee", source = "assignee.id")
    @Mapping(target = "err", constant = "")
    TaskTableResponse toTaskTableResponse(Task task);

    /**
     * Maps Task entity to TaskTableResponse DTO with custom error message.
     */
    @Mapping(target = "task_id", source = "id")
    @Mapping(target = "task_status", source = "status")
    @Mapping(target = "task_assignee", source = "assignee.id")
    @Mapping(target = "err", source = "errorMessage")
    TaskTableResponse toTaskTableResponseWithError(Task task, String errorMessage);

    // ==================== LIST MAPPINGS ====================

    /**
     * Maps list of Users to list of UserResponse DTOs.
     */
    java.util.List<UserResponse> toUserResponseList(java.util.List<User> users);

    /**
     * Maps list of Tasks to list of TaskResponse DTOs.
     */
    java.util.List<TaskResponse> toTaskResponseList(java.util.List<Task> tasks);

    /**
     * Maps list of Comments to list of CommentResponse DTOs.
     */
    java.util.List<CommentResponse> toCommentResponseList(java.util.List<Comment> comments);

    /**
     * Maps list of Comments to list of CommentsResponse DTOs.
     */
    java.util.List<CommentsResponse> toCommentsResponseList(java.util.List<Comment> comments);

    /**
     * Maps list of Tasks to list of TaskTableResponse DTOs.
     */
    java.util.List<TaskTableResponse> toTaskTableResponseList(java.util.List<Task> tasks);
}

