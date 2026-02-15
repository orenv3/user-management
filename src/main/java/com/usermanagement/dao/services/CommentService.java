package com.usermanagement.dao.services;


import com.usermanagement.entities.Comment;
import com.usermanagement.entities.Task;
import com.usermanagement.errorHandler.CommentGeneralErrorException;
import com.usermanagement.mappers.EntityMapper;
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
    private final EntityMapper entityMapper;
    

    public CommentResponse createComment(AdminCreateCommentRequest commentObj) {
        Task taskToComment = taskRepo.getTaskById(commentObj.taskId());
        if (taskToComment.getAssignee() == null)
            throw new CommentGeneralErrorException("In order to comment assignee in the task is a must. task: " + taskToComment);

        Comment comment = entityMapper.toEntity(commentObj);
        comment.setTaskId(taskToComment);
        comment.setUserId(taskToComment.getAssignee());
        Comment saved = commentRepo.save(comment);
        return entityMapper.toCommentResponse(saved);
    }

    public CommentsResponse userCommentOnTask(UserTaskCommentRequest commentObj) {
        Task taskToComment = taskRepo.getTaskById(commentObj.taskId());
        if (taskToComment.getAssignee() == null) {
            throw new CommentGeneralErrorException("In order to comment assignee in the task is a must. task: " + taskToComment);
        } else if (taskToComment.getAssignee().getId() != commentObj.userId()) {
            throw new CommentGeneralErrorException("The user with id: " + commentObj.userId() + " can not comment on task " + taskToComment);
        }

        Comment comment = entityMapper.toEntity(commentObj);
        comment.setTaskId(taskToComment);
        comment.setUserId(taskToComment.getAssignee());
        Comment response = commentRepo.save(comment);
        return entityMapper.toCommentsResponseWithError(response, "Comment added successfully");
    }


    public CommentResponse updateComment(UpdateCommentRequest commentObj) {
        Comment comment = commentRepo.getReferenceById(commentObj.id());
        entityMapper.updateCommentFromRequest(commentObj, comment);
        Comment saved = commentRepo.save(comment);
        return entityMapper.toCommentResponse(saved);
    }

    public List<CommentResponse> getAllCommentList() {
        List<Comment> commentList = commentRepo.findAll();
        return entityMapper.toCommentResponseList(commentList);
    }

    public List<CommentsResponse> getAllUserCommentList(long userId) {
        List<TaskTableResponse> userTasks = taskRepo.getAllUserTaskList(userId);
        List<String> taskNames = userTasks.stream().parallel().map(tsk -> tsk.title()).collect(Collectors.toList());
        List<Comment> commentList = commentRepo.findByTaskId_TitleIn(taskNames);

        return entityMapper.toCommentsResponseList(commentList);
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

        return entityMapper.toCommentsResponseList(commentList);
    }
}

