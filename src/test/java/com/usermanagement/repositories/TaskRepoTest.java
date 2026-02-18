package com.usermanagement.repositories;

import com.usermanagement.entities.Task;
import com.usermanagement.entities.User;
import com.usermanagement.requestObjects.CreateTaskRequest;
import com.usermanagement.requestObjects.CreateUserRequest;
import com.usermanagement.utils.TaskStatus;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
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

    @Autowired
    private EntityManager entityManager;

    private TaskStatus taskStatus = new TaskStatus();

    /** Set in @BeforeEach: user at index 2 (assigned to task3 and task6). */
    private long assigneeIdForTests;
    /** Set in @BeforeEach: task at index 2 (third task). */
    private long taskIdForTests;

    @BeforeEach
    void setDbTasksAndAssignees() {
        assignUser2Task();
    }

    @Test
    void getAllByAssignee() {
        String archivedStatus = taskStatus.getARCHIVED();
        List<Task> tasks = taskRepoUnderTest.getAllByAssignee(assigneeIdForTests, archivedStatus);
        assertThat(tasks).isNotNull();
        assertThat(tasks).hasSize(2);
    }

    @Test
    void getAllByAssignee_taskWithRelevantAssigneeAndStatusIsNotArchived() {
        String archivedStatus = taskStatus.getARCHIVED();
        List<Task> tasks = taskRepoUnderTest.getAllByAssignee(assigneeIdForTests, archivedStatus);
        assertThat(tasks).isNotEmpty();
        for (Task task : tasks) {
            assertThat(task.getAssignee()).isNotNull();
            assertThat(task.getAssignee().getId()).isEqualTo(assigneeIdForTests);
            assertThat(task.getStatus()).isNotEqualTo(archivedStatus);
        }
    }

    @Test
    void getAllByAssignee_taskWithStatusEquals_NotArchived() {
        String archivedStatus = taskStatus.getARCHIVED();
        List<Task> tasks = taskRepoUnderTest.getAllByAssignee(assigneeIdForTests, archivedStatus);
        assertThat(tasks).hasSize(2);
        for (Task task : tasks) {
            assertThat(task.getStatus()).isNotEqualTo(archivedStatus);
        }
        Task taskToArchive = taskRepoUnderTest.findById(taskIdForTests).orElseThrow();
        taskToArchive.setStatus(archivedStatus);
        taskRepoUnderTest.saveAndFlush(taskToArchive);
        tasks = taskRepoUnderTest.getAllByAssignee(assigneeIdForTests, archivedStatus);
        assertThat(tasks).hasSize(1);
        for (Task task : tasks) {
            assertThat(task.getStatus()).isNotEqualTo(archivedStatus);
        }
    }

    @Test
    void updateTaskToComplete() {
        String completedStatus = taskStatus.getCOMPLETED();
        Task taskBeforeUpdate = taskRepoUnderTest.findById(taskIdForTests).orElseThrow();
        assertThat(taskBeforeUpdate.getStatus()).isNotEqualTo(completedStatus);
        taskRepoUnderTest.updateTaskToComplete(taskIdForTests, completedStatus);
        Task taskAfterUpdate = taskRepoUnderTest.findById(taskIdForTests).orElseThrow();
        assertThat(taskAfterUpdate.getStatus()).isEqualTo(completedStatus);
    }






    /**
     * Helper method to set up test data: creates users and tasks, then assigns users to tasks.
     */
    private void assignUser2Task() {
        List<User> users = setUsersToDb();
        List<Task> tasks = createTasks();
        assigneeIdForTests = users.get(2).getId();
        taskIdForTests = tasks.get(2).getId();
        Task task1 = tasks.get(0), task2 = tasks.get(1), task3 = tasks.get(2), task4 = tasks.get(3),
                task5 = tasks.get(4), task6 = tasks.get(5), task7 = tasks.get(6), task8 = tasks.get(7);
        User user1 = users.get(0), user2 = users.get(1), user3 = users.get(2), user4 = users.get(3), user5 = users.get(4);

        task1.setAssignee(user1);
        taskRepoUnderTest.save(task1);
        task2.setAssignee(user2);
        taskRepoUnderTest.save(task2);
        task3.setAssignee(user3);
        taskRepoUnderTest.save(task3);
        task4.setAssignee(user4);
        taskRepoUnderTest.save(task4);
        task5.setAssignee(user5);
        taskRepoUnderTest.save(task5);
        task6.setAssignee(user3);
        taskRepoUnderTest.save(task6);
        task7.setAssignee(user4);
        taskRepoUnderTest.save(task7);
        task8.setAssignee(user5);
        taskRepoUnderTest.save(task8);
        entityManager.flush();
    }

    /**
     * Helper method to create test users in the database. Returns saved users in order.
     */
    private List<User> setUsersToDb() {
        return List.of(
                userRepoDbBuilder.save(new User(new CreateUserRequest("oren", "oren@email1", true, true, "pass"))),
                userRepoDbBuilder.save(new User(new CreateUserRequest("avivit", "avivit@email1", true, true, "pass"))),
                userRepoDbBuilder.save(new User(new CreateUserRequest("maya", "maya@email1", false, true, "pass"))),
                userRepoDbBuilder.save(new User(new CreateUserRequest("Daniel", "Daniel@email1", false, true, "pass"))),
                userRepoDbBuilder.save(new User(new CreateUserRequest("raz", "raz@email1", false, true, "pass")))
        );
    }

    /**
     * Helper method to create test tasks in the database. Returns saved tasks in order.
     */
    private List<Task> createTasks() {
        return List.of(
                taskRepoUnderTest.save(new Task(new CreateTaskRequest("task1", "task1", "PENDING"))),
                taskRepoUnderTest.save(new Task(new CreateTaskRequest("task2", "task2", "PENDING"))),
                taskRepoUnderTest.save(new Task(new CreateTaskRequest("task3", "task3", "PENDING"))),
                taskRepoUnderTest.save(new Task(new CreateTaskRequest("task4", "task4", "PENDING"))),
                taskRepoUnderTest.save(new Task(new CreateTaskRequest("task5", "task5", "PENDING"))),
                taskRepoUnderTest.save(new Task(new CreateTaskRequest("task6", "task6", "PENDING"))),
                taskRepoUnderTest.save(new Task(new CreateTaskRequest("task7", "task7", "PENDING"))),
                taskRepoUnderTest.save(new Task(new CreateTaskRequest("task8", "task8", "PENDING")))
        );
    }
}