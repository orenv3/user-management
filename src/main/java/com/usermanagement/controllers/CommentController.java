package com.usermanagement.controllers;

import com.usermanagement.requestObjects.UserTaskCommentRequest;
import com.usermanagement.requestObjects.AdminCreateCommentRequest;
import com.usermanagement.requestObjects.UpdateCommentRequest;
import com.usermanagement.responseObjects.CommentResponse;
import com.usermanagement.responseObjects.CommentsResponse;
import com.usermanagement.dao.services.CommentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    private final CommentService commentService;

    /**
     * Admin privilege
     * Create comment -  comment on any task that belong to user
     * @param commentObj
     * @return
     */
    @PostMapping("admin/createComment")
    public CommentResponse create(@Valid @RequestBody() AdminCreateCommentRequest commentObj) {
        return commentService.createComment(commentObj);
    }

    /**
     * Admin privilege
     * Update Comment by comment ID
     * @param commentObj
     * @return
     */
    @PutMapping("admin/updateComment")
    public CommentResponse update(@Valid @RequestBody() UpdateCommentRequest commentObj) {
        return commentService.updateComment(commentObj);
    }

/**
 * Admin privilege
 * Get list of all comments in DB
 */
    @GetMapping("admin/allCommentList")
    public List<CommentResponse> getAllCommentList(){
        return commentService.getAllCommentList();
    }


    /**
     * USER privilege
     * User can comment his tasks - comment specific task
     * @param commentObj
     * @return
     */
    @PostMapping("user/commentMyTask")
    public CommentsResponse userCommentOnTask(@Valid @RequestBody() UserTaskCommentRequest commentObj) {
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
    public List<CommentsResponse> getAllUserCommentList( @PathVariable("userId") long userId){
        return commentService.getAllUserCommentList(userId);
    }

    @GetMapping("user/userCommentListViaNativeQuery/{userId}")
    public List<CommentsResponse> getAllUserCommentListViaNativeQuery( @PathVariable("userId") long userId){
        return commentService.getAllUserCommentListViaNativeQuery(userId);
    }



}
