package com.usermanagement.dao.services;


import com.usermanagement.entities.Comment;
import com.usermanagement.entities.Task;
import com.usermanagement.errorHandler.CommentGeneralErrorException;
import com.usermanagement.repositories.CommentRepo;
import com.usermanagement.requestObjects.UserTaskCommentRequest;
import com.usermanagement.requestObjects.AdminCreateCommentRequest;
import com.usermanagement.requestObjects.UpdateCommentRequest;
import com.usermanagement.responseObjects.CommentResponse;
import com.usermanagement.responseObjects.CommentsResponse;
import com.usermanagement.responseObjects.TaskTableResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service("CommentImpl")
public class CommentService {

    private final CommentRepo commentRepo;
    private final TaskService taskRepo;
    private final UserService userRepo;
    private final EntityManager entityManager;


    public CommentResponse createComment(AdminCreateCommentRequest commentObj) {
        Task taskToComment = taskRepo.getTaskById(commentObj.taskId());
        if (taskToComment.getAssignee() == null)
            throw new CommentGeneralErrorException("In order to comment assignee in the task is a must. task: " + taskToComment);

        Comment comment = new Comment(commentObj);
        comment.setTaskId(taskToComment);
        comment.setUserId(taskToComment.getAssignee());
        Comment saved = commentRepo.save(comment);
        return mapToCommentResponse(saved);
    }

    public CommentsResponse userCommentOnTask(UserTaskCommentRequest commentObj) {
        Task taskToComment = taskRepo.getTaskById(commentObj.taskId());
        if (taskToComment.getAssignee() == null) {
            throw new CommentGeneralErrorException("In order to comment assignee in the task is a must. task: " + taskToComment);
        } else if (taskToComment.getAssignee().getId() != commentObj.userId()) {
            throw new CommentGeneralErrorException("The user with id: " + commentObj.userId() + " can not comment on task " + taskToComment);
        }

        Comment comment = new Comment(commentObj);
        comment.setTaskId(taskToComment);
        comment.setUserId(taskToComment.getAssignee());
        Comment response = commentRepo.save(comment);
        return new CommentsResponse(
                response.getTimestamp(),
                response.getComment(),
                response.getUserId().getId(),
                response.getTaskId().getId(),
                response.getTaskId().getTitle(),
                "Comment added successfully");
    }


    public CommentResponse updateComment(UpdateCommentRequest commentObj) {
        Comment comment = commentRepo.getReferenceById(commentObj.id());
        comment = commentObj.updateCommentParameters(commentObj, comment);
        Comment saved = commentRepo.save(comment);
        return mapToCommentResponse(saved);
    }

    public List<CommentResponse> getAllCommentList() {
        List<Comment> commentList = commentRepo.findAll();
        return commentList.stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());
    }

    public List<CommentsResponse> getAllUserCommentList(long userId) {
        List<TaskTableResponse> userTasks = taskRepo.getAllUserTaskList(userId);
        List<String> taskNames = userTasks.stream().parallel().map(tsk -> tsk.title()).collect(Collectors.toList());
        List<Comment> commentList = commentRepo.findByTaskId_TitleIn(taskNames);

        List<CommentsResponse> response = commentList.stream().parallel().map(com ->
                new CommentsResponse(
                        com.getTimestamp(),
                        com.getComment(),
                        com.getUserId().getId(),
                        com.getTaskId().getId(),
                        com.getTaskId().getTitle(), "")).collect(Collectors.toList());

        return response;
    }

    public List<CommentsResponse> getAllUserCommentListViaNativeQuery(long userId) {

        List<Comment> commentList = entityManager.createQuery("""
    SELECT c 
     FROM Comment c 
     WHERE c.taskId IN ( SELECT t.id
     FROM Task t 
     WHERE c.taskId = t.id 
     AND t.status != 'ARCHIVED' 
     AND t.assignee.id = :userId)""", Comment.class)
                .setParameter("userId", userId)
                .getResultList();


        List<CommentsResponse> response = commentList.stream().parallel().map(com ->
                new CommentsResponse(
                        com.getTimestamp(),
                        com.getComment(),
                        com.getUserId().getId(),
                        com.getTaskId().getId(),
                        com.getTaskId().getTitle(), "")).collect(Collectors.toList());
        return response;
    }

    private CommentResponse mapToCommentResponse(Comment comment) {
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

