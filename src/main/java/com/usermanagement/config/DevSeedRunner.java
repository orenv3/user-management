package com.usermanagement.config;

import com.usermanagement.dao.services.TaskService;
import com.usermanagement.errorHandler.TaskGeneralErrorException;
import com.usermanagement.errorHandler.UserValidationErrorException;
import com.usermanagement.requestObjects.CreateTaskRequest;
import com.usermanagement.requestObjects.CreateUserRequest;
import com.usermanagement.security.AuthResponse;
import com.usermanagement.security.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Profile("dev-seed")
public class DevSeedRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DevSeedRunner.class);

    private final AuthenticationService authenticationService;
    private final TaskService taskService;
    private final Environment environment;

    public DevSeedRunner(
            AuthenticationService authenticationService,
            TaskService taskService,
            Environment environment) {
        this.authenticationService = authenticationService;
        this.taskService = taskService;
        this.environment = environment;
    }

    @Override
    public void run(String... args) throws UserValidationErrorException, TaskGeneralErrorException {
        if (!requiredAdminVarsPresent()) {
            log.error(
                    "dev-seed skipped: set SEED_ADMIN1_* and SEED_ADMIN2_* (email, password, name) in .env");
            return;
        }
        if (!requiredUserVarsPresent()) {
            log.error(
                    "dev-seed skipped: set SEED_USER1_* and SEED_USER2_* (email, password, name) in .env");
            return;
        }

        seedUser(
                "admin1",
                env("SEED_ADMIN1_NAME"),
                env("SEED_ADMIN1_EMAIL"),
                env("SEED_ADMIN1_PASSWORD"),
                true);
        seedUser(
                "admin2",
                env("SEED_ADMIN2_NAME"),
                env("SEED_ADMIN2_EMAIL"),
                env("SEED_ADMIN2_PASSWORD"),
                true);
        seedUser(
                "user1",
                env("SEED_USER1_NAME"),
                env("SEED_USER1_EMAIL"),
                env("SEED_USER1_PASSWORD"),
                false);
        seedUser(
                "user2",
                env("SEED_USER2_NAME"),
                env("SEED_USER2_EMAIL"),
                env("SEED_USER2_PASSWORD"),
                false);

        CreateTaskRequest task1 = new CreateTaskRequest("task1", "task1", null);
        CreateTaskRequest task2 = new CreateTaskRequest("task2", "task2", null);
        CreateTaskRequest task3 = new CreateTaskRequest("task3", "task3", null);
        CreateTaskRequest task4 = new CreateTaskRequest("task4", "task4", null);
        log.info("Seed task1: {}", taskService.createTask(task1));
        log.info("Seed task2: {}", taskService.createTask(task2));
        log.info("Seed task3: {}", taskService.createTask(task3));
        log.info("Seed task4: {}", taskService.createTask(task4));
        taskService.assignUserToTask(1, 2);
        taskService.assignUserToTask(2, 2);
        taskService.assignUserToTask(3, 3);
        taskService.assignUserToTask(4, 3);
    }

    private void seedUser(String label, String name, String email, String password, boolean isAdmin)
            throws UserValidationErrorException {
        CreateUserRequest request = new CreateUserRequest(name, email, isAdmin, true, password);
        AuthResponse response = authenticationService.registerSeedUser(request);
        log.info("Seed {}: {}", label, response);
    }

    private boolean requiredAdminVarsPresent() {
        return allPresent(
                "SEED_ADMIN1_EMAIL",
                "SEED_ADMIN1_PASSWORD",
                "SEED_ADMIN1_NAME",
                "SEED_ADMIN2_EMAIL",
                "SEED_ADMIN2_PASSWORD",
                "SEED_ADMIN2_NAME");
    }

    private boolean requiredUserVarsPresent() {
        return allPresent(
                "SEED_USER1_EMAIL",
                "SEED_USER1_PASSWORD",
                "SEED_USER1_NAME",
                "SEED_USER2_EMAIL",
                "SEED_USER2_PASSWORD",
                "SEED_USER2_NAME");
    }

    private boolean allPresent(String... keys) {
        for (String key : keys) {
            if (isBlank(env(key))) {
                log.warn("Missing or blank env: {}", key);
                return false;
            }
        }
        return true;
    }

    private String env(String key) {
        return environment.getProperty(key);
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
