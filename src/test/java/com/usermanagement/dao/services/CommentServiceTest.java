package com.usermanagement.dao.services;

import com.usermanagement.entities.Comment;
import com.usermanagement.entities.Task;
import com.usermanagement.entities.Users;
import com.usermanagement.errorHandler.CommentGeneralErrorException;
import com.usermanagement.mappers.EntityMapper;
import com.usermanagement.repositories.CommentRepo;
import com.usermanagement.requestObjects.AdminCreateCommentRequest;
import com.usermanagement.requestObjects.UserTaskCommentRequest;
import com.usermanagement.responseObjects.CommentsResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CommentServiceTest {

    @Mock
    private CommentRepo commentRepo;
    @Mock
    private TaskService taskService;
    @Mock
    private EntityManager entityManager;
    @Mock
    private EntityMapper entityMapper;

    private AutoCloseable mocks;
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        commentService = new CommentService(commentRepo, taskService, entityManager, entityMapper);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void createComment_rejectsTaskWithoutAssignee() {
        Task task = new Task();
        task.setId(10L);
        task.setAssignee(null);
        when(taskService.getTaskById(10L)).thenReturn(task);

        assertThatThrownBy(() -> commentService.createComment(new AdminCreateCommentRequest("hi", 10L)))
                .isInstanceOf(CommentGeneralErrorException.class)
                .hasMessageContaining("assignee");
        verify(commentRepo, never()).save(any());
    }

    @Test
    void userCommentOnTask_rejectsWhenUserNotAssignee() {
        Users assignee = new Users();
        assignee.setId(5L);
        assignee.setActive(true);
        assignee.setAdmin(false);

        Task task = new Task();
        task.setId(99L);
        task.setAssignee(assignee);
        when(taskService.getTaskById(99L)).thenReturn(task);

        UserTaskCommentRequest req = new UserTaskCommentRequest("hack", 99L, 123L);
        assertThatThrownBy(() -> commentService.userCommentOnTask(req))
                .isInstanceOf(CommentGeneralErrorException.class)
                .hasMessageContaining("can not comment");
        verify(commentRepo, never()).save(any());
    }

    @Test
    void userCommentOnTask_persistsWhenUserIsAssignee() {
        Users assignee = new Users();
        assignee.setId(7L);
        assignee.setActive(true);
        assignee.setAdmin(false);

        Task task = new Task();
        task.setId(101L);
        task.setAssignee(assignee);
        when(taskService.getTaskById(101L)).thenReturn(task);

        when(entityMapper.toEntity(any(UserTaskCommentRequest.class))).thenReturn(new Comment());
        when(commentRepo.save(any(Comment.class))).thenAnswer(inv -> inv.getArgument(0));
        when(entityMapper.toCommentsResponseWithError(any(Comment.class), any(String.class)))
                .thenAnswer(inv -> {
                    Comment c = inv.getArgument(0);
                    String msg = inv.getArgument(1);
                    Long taskId = c.getTaskId() == null ? null : c.getTaskId().getId();
                    Long userId = c.getUserId() == null ? null : c.getUserId().getId();
                    return new CommentsResponse(new java.util.Date(), "n/a", userId, taskId, "n/a", msg);
                });

        CommentsResponse out = commentService.userCommentOnTask(new UserTaskCommentRequest("ok", 101L, 7L));

        assertThat(out.err()).contains("successfully");
        verify(commentRepo).save(any(Comment.class));
    }
}

