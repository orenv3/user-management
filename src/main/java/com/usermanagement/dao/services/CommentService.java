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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service("CommentImpl")
public class CommentService {

    private static final Logger log = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepo commentRepo;
    private final TaskService taskRepo;
    // private final UserService userRepo;
    private final EntityManager entityManager;
    private final EntityMapper entityMapper;
    

    public CommentResponse createComment(AdminCreateCommentRequest commentObj) {
        log.info("Create comment requested (admin). taskId={}", commentObj.taskId());
        Task taskToComment = taskRepo.getTaskById(commentObj.taskId());
        if (taskToComment.getAssignee() == null)
            throw new CommentGeneralErrorException("In order to comment assignee in the task is a must. task: " + taskToComment);

        Comment comment = entityMapper.toEntity(commentObj);
        comment.setTaskId(taskToComment);
        comment.setUserId(taskToComment.getAssignee());
        Comment saved = commentRepo.save(comment);
        log.info("Comment created. id={} taskId={} userId={}", saved.getId(), taskToComment.getId(), saved.getUserId().getId());
        return entityMapper.toCommentResponse(saved);
    }

    public CommentsResponse userCommentOnTask(UserTaskCommentRequest commentObj) {
        log.info("User comment requested. taskId={} userId={}", commentObj.taskId(), commentObj.userId());
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
        log.info("User comment created. id={} taskId={} userId={}", response.getId(), taskToComment.getId(), response.getUserId().getId());
        return entityMapper.toCommentsResponseWithError(response, "Comment added successfully");
    }


    public CommentResponse updateComment(UpdateCommentRequest commentObj) {
        log.info("Update comment requested. id={}", commentObj.id());
        Comment comment = commentRepo.getReferenceById(commentObj.id());
        entityMapper.updateCommentFromRequest(commentObj, comment);
        Comment saved = commentRepo.save(comment);
        log.info("Comment updated. id={}", saved.getId());
        return entityMapper.toCommentResponse(saved);
    }

    public List<CommentResponse> getAllCommentList() {
        log.info("Get all comments requested.");
        List<Comment> commentList = commentRepo.findAll();
        log.info("Get all comments result count={}", commentList.size());
        return entityMapper.toCommentResponseList(commentList);
    }

    public List<CommentsResponse> getAllUserCommentList(long userId) {
        log.info("Get all user comments requested. userId={}", userId);
        List<TaskTableResponse> userTasks = taskRepo.getAllUserTaskList(userId);
        List<String> taskNames = userTasks.stream().parallel().map(tsk -> tsk.task_title()).collect(Collectors.toList());
        List<Comment> commentList = commentRepo.findByTaskId_TitleIn(taskNames);

        log.info("Get all user comments result count={} userId={}", commentList.size(), userId);
        return entityMapper.toCommentsResponseList(commentList);
    }

    public List<CommentsResponse> getAllUserCommentListViaNativeQuery(long userId) {
        log.info("Get all user comments via native query requested. userId={}", userId);
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

        log.info("Get all user comments via native query result count={} userId={}", commentList.size(), userId);
        return entityMapper.toCommentsResponseList(commentList);
    }
}

