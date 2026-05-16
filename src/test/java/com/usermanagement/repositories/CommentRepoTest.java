package com.usermanagement.repositories;

import com.usermanagement.entities.Comment;
import com.usermanagement.entities.Task;
import com.usermanagement.entities.Users;
import com.usermanagement.requestObjects.CreateTaskRequest;
import com.usermanagement.requestObjects.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class CommentRepoTest {


    @Autowired
    private CommentRepo commentRepoUnderTest;

    @Autowired
    private TaskRepo taskRepoDbBuilder;

    @Autowired
    private UserRepo userRepoDbBuilder;

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


    @BeforeEach
    void setUp() {
        assignUser2Task();
    }

    @Test
    void findByTaskId_TitleIn() {
        List<String> taskTitles = List.of("task1", "task2", "task3");

        List<Comment> comments = commentRepoUnderTest.findByTaskId_TitleIn(taskTitles);

        assertThat(comments).isNotNull();
        comments.forEach(comment -> {
            assertThat(comment.getTaskId()).isNotNull();
            assertThat(taskTitles).contains(comment.getTaskId().getTitle());
        });
    }


    private void assignUser2Task() {
        setUsersToDb();
        createTasks();
        t1.setAssignee(u1);
        taskRepoDbBuilder.save(t1);
        t2.setAssignee(u2);
        taskRepoDbBuilder.save(t2);
        t3.setAssignee(u3);
        taskRepoDbBuilder.save(t3);
        t4.setAssignee(u4);
        taskRepoDbBuilder.save(t4);
        t5.setAssignee(u5);
        taskRepoDbBuilder.save(t5);
        t6.setAssignee(u3);
        taskRepoDbBuilder.save(t6);
        t7.setAssignee(u4);
        taskRepoDbBuilder.save(t7);
        t8.setAssignee(u5);
        taskRepoDbBuilder.save(t8);
    }

    private void setUsersToDb() {
        u1 = userRepoDbBuilder.save(new Users(new CreateUserRequest(
                "oren", "oren@email1",
                true, true,
                "pass")));

        u2 = userRepoDbBuilder.save(new Users(new CreateUserRequest(
                "avivit", "avivit@email1",
                true, true,
                "pass")));

        u3 = userRepoDbBuilder.save(new Users(new CreateUserRequest(
                "maya", "maya@email1",
                false, true,
                "pass")));

        u4 = userRepoDbBuilder.save(new Users(new CreateUserRequest(
                "Daniel", "Daniel@email1",
                false, true,
                "pass")));

        u5 = userRepoDbBuilder.save(new Users(new CreateUserRequest(
                "raz", "raz@email1",
                false, true,
                "pass")));
    }

    private void createTasks() {
        t1 = taskRepoDbBuilder.save(new Task(new CreateTaskRequest(
                "task1",
                "task1",
                "PENDING"
        )));

        t2 = taskRepoDbBuilder.save(new Task(new CreateTaskRequest(
                "task2",
                "task2",
                "PENDING"
        )));

        t3 = taskRepoDbBuilder.save(new Task(new CreateTaskRequest(
                "task3",
                "task3",
                "PENDING"
        )));

        t4 = taskRepoDbBuilder.save(new Task(new CreateTaskRequest(
                "task4",
                "task4",
                "PENDING"
        )));

        t5 = taskRepoDbBuilder.save(new Task(new CreateTaskRequest(
                "task5",
                "task5",
                "PENDING"
        )));

        t6 = taskRepoDbBuilder.save(new Task(new CreateTaskRequest(
                "task6",
                "task6",
                "PENDING"
        )));

        t7 = taskRepoDbBuilder.save(new Task(new CreateTaskRequest(
                "task7",
                "task7",
                "PENDING"
        )));

        t8 = taskRepoDbBuilder.save(new Task(new CreateTaskRequest(
                "task8",
                "task8",
                "PENDING"
        )));
    }
}
