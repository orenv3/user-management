package com.usermanagement.dao.services;

import com.usermanagement.entities.Task;
import com.usermanagement.errorHandler.TaskGeneralErrorException;
import com.usermanagement.mappers.EntityMapper;
import com.usermanagement.repositories.TaskRepo;
import com.usermanagement.requestObjects.CreateTaskRequest;
import com.usermanagement.responseObjects.TaskResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaskServiceTest {

    @Mock
    private TaskRepo taskRepo;
    @Mock
    private UserService userService;
    @Mock
    private EntityMapper entityMapper;

    private AutoCloseable mocks;
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        taskService = new TaskService(taskRepo, userService, entityMapper);
        when(entityMapper.toEntity(any(CreateTaskRequest.class))).thenAnswer(inv -> new Task(inv.getArgument(0)));
        when(entityMapper.toTaskResponse(any(Task.class))).thenAnswer(inv -> {
            Task t = inv.getArgument(0);
            Long assigneeId = t.getAssignee() == null ? null : t.getAssignee().getId();
            return new TaskResponse(t.getId(), t.getTitle(), t.getDescription(), t.getStatus(), assigneeId);
        });
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void createTask_rejectsDuplicateTitle() {
        CreateTaskRequest req = new CreateTaskRequest("dup-title", "desc", "PENDING");
        Task existing = new Task(req);
        existing.setId(5L);
        when(taskRepo.findByTitle("dup-title")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> taskService.createTask(req))
                .isInstanceOf(TaskGeneralErrorException.class)
                .hasMessageContaining("same title");
        verify(taskRepo, never()).save(any());
    }

    @Test
    void createTask_persistsWhenTitleIsUnique() throws TaskGeneralErrorException {
        CreateTaskRequest req = new CreateTaskRequest("unique-t", "d", "PENDING");
        when(taskRepo.findByTitle("unique-t")).thenReturn(Optional.empty());
        when(taskRepo.save(any(Task.class))).thenAnswer(inv -> {
            Task t = inv.getArgument(0);
            t.setId(10L);
            return t;
        });

        TaskResponse out = taskService.createTask(req);

        assertThat(out.id()).isEqualTo(10L);
        assertThat(out.title()).isEqualTo("unique-t");
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepo).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("PENDING");
    }
}
