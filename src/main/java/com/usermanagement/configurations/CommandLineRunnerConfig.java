package com.usermanagement.configurations;

import com.usermanagement.services.TaskService;
import com.usermanagement.requestObjects.CreateTaskRequest;
import com.usermanagement.requestObjects.CreateUserRequest;
import com.usermanagement.security.AuthenticationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Registers the application's CommandLineRunner for dev/production.
 * Excluded when profile "test" is active so slice tests (e.g. @DataJpaTest) do
 * not require
 * AuthenticationService or TaskService.
 */
@Configuration
public class CommandLineRunnerConfig {

        @Bean
        @Profile("!test")
        CommandLineRunner commandLineRunner(AuthenticationService service, TaskService srv) {
                return args -> {
                        CreateUserRequest createAdmin = new CreateUserRequest("oren", "orenv@vinogura",
                                        true, true, "1234");
                        System.out.println("createAdmin: " + service.registerUser(createAdmin));

                        CreateUserRequest createAdminForTempUser = new CreateUserRequest("tempUser", "tempUser@email1",
                                        true, true, "temp1234");
                        System.out.println("createAdminForTempUser: " + service.registerUser(createAdminForTempUser));

                        CreateUserRequest createUser1 = new CreateUserRequest("user", "user",
                                        false, true, "pass");
                        System.out.println("createUser1: " + service.registerUser(createUser1));

                        CreateUserRequest createUser2 = new CreateUserRequest("user2", "user2",
                                        false, true, "pass");
                        System.out.println("createUser2: " + service.registerUser(createUser2));

                        CreateTaskRequest task1 = new CreateTaskRequest("task1", "task1", null);
                        CreateTaskRequest task2 = new CreateTaskRequest("task2", "task2", null);
                        CreateTaskRequest task3 = new CreateTaskRequest("task3", "task3", null);
                        CreateTaskRequest task4 = new CreateTaskRequest("task4", "task4", null);
                        srv.createTask(task1);
                        srv.createTask(task2);
                        srv.createTask(task3);
                        srv.createTask(task4);
                        srv.assignUserToTask(1, 2);
                        srv.assignUserToTask(2, 2);
                        srv.assignUserToTask(3, 3);
                        srv.assignUserToTask(4, 3);
                };
        }
}
