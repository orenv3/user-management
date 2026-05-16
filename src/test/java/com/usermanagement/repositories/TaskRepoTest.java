package com.usermanagement.repositories;

import com.usermanagement.entities.Task;
import com.usermanagement.entities.Users;
import com.usermanagement.requestObjects.CreateTaskRequest;
import com.usermanagement.requestObjects.CreateUserRequest;
import com.usermanagement.utils.TaskStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class TaskRepoTest {


    @Autowired
    private TaskRepo taskRepoUnderTest;

    @Autowired
    private UserRepo userRepoDbBuilder;

    private final TaskStatus taskStatus = new TaskStatus();

    private Users u1;
    private Users u2;
    private Users u3;
    private Users u4;
    private Users u5;
    private Task t1;
    private Task t2;
    private Task t3;
    private Task t4;
    private Task t5;
    private Task t6;
    private Task t7;
    private Task t8;


    @BeforeAll
    void setDbTasksAndAssignees() {
        assignUser2Task();
    }

    @Test
    void getAllByAssignee() {
        long assignee = u3.getId();
        String archivedStatus = taskStatus.getARCHIVED();

        List<Task> tasks = taskRepoUnderTest.getAllByAssignee(assignee, archivedStatus);

        assertThat(tasks).isNotNull();
        assertThat(tasks).hasSize(2);
    }

    @Test
    void getAllByAssignee_taskWithRelevantAssigneeAndStatusIsNotArchived() {
        long assignee = u3.getId();
        String archivedStatus = taskStatus.getARCHIVED();

        List<Task> tasks = taskRepoUnderTest.getAllByAssignee(assignee, archivedStatus);

        assertThat(tasks).isNotEmpty();
        for (Task task : tasks) {
            assertThat(task.getAssignee()).isNotNull();
            assertThat(task.getAssignee().getId()).isEqualTo(assignee);
            assertThat(task.getStatus()).isNotEqualTo(archivedStatus);
        }
    }

    @Test
    void getAllByAssignee_taskWithStatusEquals_NotArchived() {
        long assignee = u3.getId();
        String archivedStatus = taskStatus.getARCHIVED();

        List<Task> tasks = taskRepoUnderTest.getAllByAssignee(assignee, archivedStatus);

        assertThat(tasks).hasSize(2);
        for (Task task : tasks) {
            assertThat(task.getStatus()).isNotEqualTo(archivedStatus);
        }

        Task taskToArchive = taskRepoUnderTest.findById(t3.getId()).orElseThrow();
        taskToArchive.setStatus(archivedStatus);
        taskRepoUnderTest.save(taskToArchive);

        tasks = taskRepoUnderTest.getAllByAssignee(assignee, archivedStatus);
        assertThat(tasks).hasSize(1);

        for (Task task : tasks) {
            assertThat(task.getStatus()).isNotEqualTo(archivedStatus);
        }
    }


    @Test
    void updateTaskToComplete() {
        long taskId = t3.getId();
        String completedStatus = taskStatus.getCOMPLETED();
        Task taskBeforeUpdate = taskRepoUnderTest.findById(taskId).orElseThrow();

        assertThat(taskBeforeUpdate.getStatus()).isNotEqualTo(completedStatus);

        taskRepoUnderTest.updateTaskToComplete(taskId, completedStatus);

        Task taskAfterUpdate = taskRepoUnderTest.findById(taskId).orElseThrow();
        assertThat(taskAfterUpdate.getStatus()).isEqualTo(completedStatus);
    }


    private void assignUser2Task() {
        setUsersToDb();
        createTasks();
        t1.setAssignee(u1);
        taskRepoUnderTest.save(t1);
        t2.setAssignee(u2);
        taskRepoUnderTest.save(t2);
        t3.setAssignee(u3);
        taskRepoUnderTest.save(t3);
        t4.setAssignee(u4);
        taskRepoUnderTest.save(t4);
        t5.setAssignee(u5);
        taskRepoUnderTest.save(t5);
        t6.setAssignee(u3);
        taskRepoUnderTest.save(t6);
        t7.setAssignee(u4);
        taskRepoUnderTest.save(t7);
        t8.setAssignee(u5);
        taskRepoUnderTest.save(t8);
    }

    private void setUsersToDb() {
        CreateUserRequest userRequest1 = new CreateUserRequest(
                "oren", "oren@email1",
                true, true,
                "pass");
        u1 = userRepoDbBuilder.save(new Users(userRequest1));

        CreateUserRequest userRequest2 = new CreateUserRequest(
                "avivit", "avivit@email1",
                true, true,
                "pass");
        u2 = userRepoDbBuilder.save(new Users(userRequest2));

        CreateUserRequest userRequest3 = new CreateUserRequest(
                "maya", "maya@email1",
                false, true,
                "pass");
        u3 = userRepoDbBuilder.save(new Users(userRequest3));

        CreateUserRequest userRequest4 = new CreateUserRequest(
                "Daniel", "Daniel@email1",
                false, true,
                "pass");
        u4 = userRepoDbBuilder.save(new Users(userRequest4));

        CreateUserRequest userRequest5 = new CreateUserRequest(
                "raz", "raz@email1",
                false, true,
                "pass");
        u5 = userRepoDbBuilder.save(new Users(userRequest5));
    }

    private void createTasks() {
        t1 = taskRepoUnderTest.save(new Task(new CreateTaskRequest(
                "task1",
                "task1",
                "PENDING"
        )));

        t2 = taskRepoUnderTest.save(new Task(new CreateTaskRequest(
                "task2",
                "task2",
                "PENDING"
        )));

        t3 = taskRepoUnderTest.save(new Task(new CreateTaskRequest(
                "task3",
                "task3",
                "PENDING"
        )));

        t4 = taskRepoUnderTest.save(new Task(new CreateTaskRequest(
                "task4",
                "task4",
                "PENDING"
        )));

        t5 = taskRepoUnderTest.save(new Task(new CreateTaskRequest(
                "task5",
                "task5",
                "PENDING"
        )));

        t6 = taskRepoUnderTest.save(new Task(new CreateTaskRequest(
                "task6",
                "task6",
                "PENDING"
        )));

        t7 = taskRepoUnderTest.save(new Task(new CreateTaskRequest(
                "task7",
                "task7",
                "PENDING"
        )));

        t8 = taskRepoUnderTest.save(new Task(new CreateTaskRequest(
                "task8",
                "task8",
                "PENDING"
        )));
    }
}
