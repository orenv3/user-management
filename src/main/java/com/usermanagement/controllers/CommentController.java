package com.usermanagement.controllers;

import com.usermanagement.requestObjects.UserTaskCommentRequest;
import com.usermanagement.requestObjects.AdminCreateCommentRequest;
import com.usermanagement.requestObjects.UpdateCommentRequest;
import com.usermanagement.responseObjects.CommentResponse;
import com.usermanagement.responseObjects.CommentsResponse;
import com.usermanagement.dao.services.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@Validated
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "CommentController", description = "The Comment API. " +
        "Contains all the operations that can be performed on Comment table.")
@RequestMapping("/api/comment/")
@RestController
public class CommentController {

    private static final Logger log = LoggerFactory.getLogger(CommentController.class);

    private final CommentService commentService;
    
    /**
     * Admin privilege
     * Create comment -  comment on any task that belong to user
     * @param commentObj
     * @return
     */
    @PostMapping("admin/createComment")
    @Operation(summary = "Create a comment on any task (admin only)")
    public CommentResponse create(@Valid @RequestBody() AdminCreateCommentRequest commentObj) {
        logRequest("createComment", "taskId=" + commentObj.taskId());
        return commentService.createComment(commentObj);
    }

    /**
     * Admin privilege
     * Update Comment by comment ID
     * @param commentObj
     * @return
     */
    @PutMapping("admin/updateComment")
    @Operation(summary = "Update a comment (admin only)")
    public CommentResponse update(@Valid @RequestBody() UpdateCommentRequest commentObj) {
        logRequest("updateComment", "id=" + commentObj.id());
        return commentService.updateComment(commentObj);
    }

/**
 * Admin privilege
 * Get list of all comments in DB
 */
    @GetMapping("admin/allCommentList")
    @Operation(summary = "List all comments (admin only)")
    public List<CommentResponse> getAllCommentList(){
        logRequest("allCommentList", null);
        return commentService.getAllCommentList();
    }


    /**
     * USER privilege
     * User can comment his tasks - comment specific task
     * @param commentObj
     * @return
     */
    @PostMapping("user/commentMyTask")
    @Operation(summary = "Comment on a task assigned to the user (user)")
    public CommentsResponse userCommentOnTask(@Valid @RequestBody() UserTaskCommentRequest commentObj) {
        logRequest("commentMyTask", "taskId=" + commentObj.taskId() + ", userId=" + commentObj.userId());
        return commentService.userCommentOnTask(commentObj);
    }


    /**
     * USER privilege
     * Get list of user tasks and their comments
     *
     * @param userId
     * @return
     */
    @GetMapping("user/userCommentList/{userId}")
    @Operation(summary = "List comments on the user's assigned tasks (user)")
    public List<CommentsResponse> getAllUserCommentList( @PathVariable("userId") long userId){
        logRequest("userCommentList", "userId=" + userId);
        return commentService.getAllUserCommentList(userId);
    }

    @GetMapping("user/userCommentListViaNativeQuery/{userId}")
    public List<CommentsResponse> getAllUserCommentListViaNativeQuery( @PathVariable("userId") long userId){
        logRequest("userCommentListViaNativeQuery", "userId=" + userId);
        return commentService.getAllUserCommentListViaNativeQuery(userId);
    }

    private static void logRequest(String action, String details) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String principal = auth == null ? "anonymous" : String.valueOf(auth.getName());
        Object authorities = auth == null ? "[]" : auth.getAuthorities();
        if (details == null || details.isBlank()) {
            log.info("CommentController action={} principal={} authorities={}", action, principal, authorities);
        } else {
            log.info("CommentController action={} principal={} authorities={} details={}", action, principal, authorities, details);
        }
    }



}
