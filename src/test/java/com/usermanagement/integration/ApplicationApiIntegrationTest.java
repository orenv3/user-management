package com.usermanagement.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usermanagement.entities.Task;
import com.usermanagement.entities.Users;
import com.usermanagement.repositories.TaskRepo;
import com.usermanagement.repositories.UserRepo;
import com.usermanagement.requestObjects.AdminCreateCommentRequest;
import com.usermanagement.requestObjects.CreateTaskRequest;
import com.usermanagement.requestObjects.CreateUserRequest;
import com.usermanagement.requestObjects.UpdateTaskRequest;
import com.usermanagement.requestObjects.UpdateUserRequest;
import com.usermanagement.requestObjects.UserTaskCommentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ApplicationApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private TaskRepo taskRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;

    private Users admin;

    @BeforeEach
    void seedUsers() {
        persistUser("Private", "private-admin@test.com", true, true, "privatePass");
        admin = persistUser("Admin", "admin@example.com", true, true, "adminPass");
        persistUser("User", "user@example.com", false, true, "userPass");
    }

    private Users persistUser(String name, String email, boolean isAdmin, boolean active, String rawPassword) {
        CreateUserRequest req = new CreateUserRequest(name, email, isAdmin, active, rawPassword);
        Users u = new Users(req);
        u.setPassword(passwordEncoder.encode(rawPassword));
        return userRepo.save(u);
    }

    private String login(String email, String password) throws Exception {
        return objectMapper.readTree(
                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString()
        ).get("token").asText();
    }

    @Test
    void publicConfig_isAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.privateAdminEmail").value("private-admin@test.com"));
    }

    @Test
    void unauthenticatedRequestToUserApi_returns401() throws Exception {
        mockMvc.perform(get("/api/task/user/allTaskList/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithValidCredentials_returnsBearerToken() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@example.com\",\"password\":\"userPass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.userId").isNumber());
    }

    @Test
    void me_returnsCurrentUserDetails() throws Exception {
        String token = login("user@example.com", "userPass");
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.userId").isNumber());
    }

    @Test
    void inactiveUser_cannotAuthenticate() throws Exception {
        persistUser("Blocked", "blocked@example.com", false, false, "x");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"blocked@example.com\",\"password\":\"x\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void regularUser_forbiddenFromAdminTaskCreate() throws Exception {
        String token = login("user@example.com", "userPass");
        String body = objectMapper.writeValueAsString(new CreateTaskRequest("t-admin-only", "d", null));
        mockMvc.perform(post("/api/task/admin/createTask")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void admin_createTaskWithNullStatus_defaultsToPending() throws Exception {
        String token = login("admin@example.com", "adminPass");
        String body = objectMapper.writeValueAsString(new CreateTaskRequest("pending-task", "desc", null));
        mockMvc.perform(post("/api/task/admin/createTask")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void admin_cannotCreateTwoTasksWithSameTitle() throws Exception {
        String token = login("admin@example.com", "adminPass");
        CreateTaskRequest first = new CreateTaskRequest("dup", "d", null);
        mockMvc.perform(post("/api/task/admin/createTask")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(first)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/task/admin/createTask")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(first)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("same title")))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void user_seesOnlyOwnNonArchivedTasks() throws Exception {
        String adminToken = login("admin@example.com", "adminPass");
        mockMvc.perform(post("/api/task/admin/createTask")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateTaskRequest("mine", "d", null))))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/task/admin/createTask")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateTaskRequest("other", "d", null))))
                .andExpect(status().isOk());

        Task mine = taskRepo.findByTitle("mine").orElseThrow();
        Task other = taskRepo.findByTitle("other").orElseThrow();
        Users u = userRepo.findByEmail("user@example.com").orElseThrow();

        mockMvc.perform(put("/api/task/admin/assignUser" + mine.getId() + "/" + u.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
        mockMvc.perform(put("/api/task/admin/assignUser" + other.getId() + "/" + admin.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        String userToken = login("user@example.com", "userPass");
        mockMvc.perform(get("/api/task/user/allTaskList/" + u.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].task_title").value("mine"));

        mockMvc.perform(put("/api/task/admin/updateTask")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UpdateTaskRequest(mine.getId(), null, null, "ARCHIVED"))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/task/user/allTaskList/" + u.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void user_seesAllCommentsOnAssignedTask_includingFromOthers() throws Exception {
        String adminToken = login("admin@example.com", "adminPass");
        mockMvc.perform(post("/api/task/admin/createTask")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateTaskRequest("shared", "d", null))))
                .andExpect(status().isOk());
        Task task = taskRepo.findByTitle("shared").orElseThrow();
        Users u = userRepo.findByEmail("user@example.com").orElseThrow();
        mockMvc.perform(put("/api/task/admin/assignUser" + task.getId() + "/" + u.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        String userToken = login("user@example.com", "userPass");
        mockMvc.perform(post("/api/comment/user/commentMyTask")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserTaskCommentRequest("from assignee", task.getId(), u.getId()))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/comment/admin/createComment")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new AdminCreateCommentRequest("from admin", task.getId()))))
                .andExpect(status().isOk());

        String body = mockMvc.perform(get("/api/comment/user/userCommentList/" + u.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(body).contains("from assignee");
        assertThat(body).contains("from admin");
    }

    @Test
    void user_nativeCommentList_returnsCommentsOnAssignedNonArchivedTasks() throws Exception {
        String adminToken = login("admin@example.com", "adminPass");
        mockMvc.perform(post("/api/task/admin/createTask")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateTaskRequest("native-q", "d", null))))
                .andExpect(status().isOk());
        Task task = taskRepo.findByTitle("native-q").orElseThrow();
        Users u = userRepo.findByEmail("user@example.com").orElseThrow();
        mockMvc.perform(put("/api/task/admin/assignUser" + task.getId() + "/" + u.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        String userToken = login("user@example.com", "userPass");
        mockMvc.perform(post("/api/comment/user/commentMyTask")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserTaskCommentRequest("via native list", task.getId(), u.getId()))))
                .andExpect(status().isOk());

        String body = mockMvc.perform(get("/api/comment/user/userCommentListViaNativeQuery/" + u.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(body).contains("via native list");
    }

    @Test
    void user_nativeCommentList_excludesCommentsOnArchivedTasks() throws Exception {
        String adminToken = login("admin@example.com", "adminPass");
        mockMvc.perform(post("/api/task/admin/createTask")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateTaskRequest("archived", "d", null))))
                .andExpect(status().isOk());
        Task task = taskRepo.findByTitle("archived").orElseThrow();
        Users u = userRepo.findByEmail("user@example.com").orElseThrow();
        mockMvc.perform(put("/api/task/admin/assignUser" + task.getId() + "/" + u.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        String userToken = login("user@example.com", "userPass");
        mockMvc.perform(post("/api/comment/user/commentMyTask")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserTaskCommentRequest("should hide", task.getId(), u.getId()))))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/task/admin/updateTask")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UpdateTaskRequest(task.getId(), null, null, "ARCHIVED"))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/comment/user/userCommentListViaNativeQuery/" + u.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.comment == 'should hide')]").doesNotExist());
    }

    @Test
    void user_cannotCommentOnTaskAssignedToSomeoneElse() throws Exception {
        String adminToken = login("admin@example.com", "adminPass");
        mockMvc.perform(post("/api/task/admin/createTask")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateTaskRequest("alien", "d", null))))
                .andExpect(status().isOk());
        Task task = taskRepo.findByTitle("alien").orElseThrow();
        mockMvc.perform(put("/api/task/admin/assignUser" + task.getId() + "/" + admin.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        Users u = userRepo.findByEmail("user@example.com").orElseThrow();
        String userToken = login("user@example.com", "userPass");
        mockMvc.perform(post("/api/comment/user/commentMyTask")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserTaskCommentRequest("hack", task.getId(), u.getId()))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("can not comment")))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void invalidCreateUserRequest_returns400WithFieldMessage() throws Exception {
        String adminToken = login("admin@example.com", "adminPass");
        String invalid = """
                {"name":"","email":"x@y.com","isAdmin":false,"active":true,"password":"p"}
                """;
        mockMvc.perform(post("/api/auth/admin/registerUser")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Name is required")))
                .andExpect(jsonPath("$.fieldErrors.name").value(containsString("Name is required")));
    }

    @Test
    void invalidCreateUserRequest_nameTooLong_returnsDescriptiveMessage() throws Exception {
        String adminToken = login("admin@example.com", "adminPass");
        String invalid = """
                {"name":"this name is way too long","email":"x@y.com","isAdmin":false,"active":true,"password":"p"}
                """;
        mockMvc.perform(post("/api/auth/admin/registerUser")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("15 characters")));
    }

    @Test
    void invalidCreateUserRequest_invalidEmail_returns400() throws Exception {
        String adminToken = login("admin@example.com", "adminPass");
        String invalid = """
                {"name":"valid","email":"not-an-email","isAdmin":false,"active":true,"password":"p"}
                """;
        mockMvc.perform(post("/api/auth/admin/registerUser")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("valid address")))
                .andExpect(jsonPath("$.fieldErrors.email").value(containsString("valid address")));
    }

    @Test
    void login_invalidEmail_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"not-an-email\",\"password\":\"x\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").value(containsString("valid address")));
    }

    @Test
    void updateUser_invalidEmail_returns400() throws Exception {
        String adminToken = login("admin@example.com", "adminPass");
        Users regular = userRepo.findByEmail("user@example.com").orElseThrow();
        UpdateUserRequest update = new UpdateUserRequest(
                regular.getId(),
                null,
                "bad-email",
                null,
                null,
                null
        );
        mockMvc.perform(put("/api/userTable/admin/updateUser")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").value(containsString("valid address")));
    }

    @Test
    void allUserList_excludesPrivateAdmin() throws Exception {
        String adminToken = login("admin@example.com", "adminPass");
        mockMvc.perform(get("/api/userTable/admin/allUserList")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].email").value(org.hamcrest.Matchers.not(containsString("private-admin@test.com"))));
    }

    @Test
    void updatePrivateAdmin_returns403() throws Exception {
        String adminToken = login("admin@example.com", "adminPass");
        Users privateAdmin = userRepo.findByEmail("private-admin@test.com").orElseThrow();
        UpdateUserRequest update = new UpdateUserRequest(
                privateAdmin.getId(),
                "hacked",
                null,
                null,
                null,
                null
        );
        mockMvc.perform(put("/api/userTable/admin/updateUser")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(containsString("protected")));
    }

    @Test
    void analytics_recordPageView_andAdminSummary() throws Exception {
        mockMvc.perform(post("/api/analytics/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"eventType":"PAGE_VIEW","path":"/login","sessionId":"test-session"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventType").value("PAGE_VIEW"));

        String adminToken = login("admin@example.com", "adminPass");
        mockMvc.perform(get("/api/analytics/admin/summary")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPageViews").isNumber())
                .andExpect(jsonPath("$.totalLogins").isNumber());
    }

    @Test
    void invalidCreateUserRequest_returns400() throws Exception {
        String adminToken = login("admin@example.com", "adminPass");
        String invalid = """
                {"name":"","email":"x@y.com","isAdmin":false,"active":true,"password":"p"}
                """;
        mockMvc.perform(post("/api/auth/admin/registerUser")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalidCreateTaskRequest_blankTitle_returns400() throws Exception {
        String adminToken = login("admin@example.com", "adminPass");
        String invalid = """
                {"title":"   ","description":"d","status":"PENDING"}
                """;
        mockMvc.perform(post("/api/task/admin/createTask")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest());
    }

    @Test
    void admin_deactivatesUser_userCannotLogin() throws Exception {
        String adminToken = login("admin@example.com", "adminPass");
        Users regular = userRepo.findByEmail("user@example.com").orElseThrow();
        UpdateUserRequest deactivate = new UpdateUserRequest(
                regular.getId(),
                regular.getName(),
                regular.getEmail(),
                regular.isAdmin(),
                false,
                null
        );
        mockMvc.perform(put("/api/userTable/admin/updateUser")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deactivate)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@example.com\",\"password\":\"userPass\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void admin_updateTask_omittingStatus_preservesExistingStatus() throws Exception {
        String adminToken = login("admin@example.com", "adminPass");
        mockMvc.perform(post("/api/task/admin/createTask")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateTaskRequest("status-hold", "d", "PENDING"))))
                .andExpect(status().isOk());
        Task task = taskRepo.findByTitle("status-hold").orElseThrow();
        mockMvc.perform(put("/api/task/admin/updateTask")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UpdateTaskRequest(task.getId(), "new-title", null, null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.title").value("new-title"));
    }

    @Test
    void spaRoute_users_servesIndexWithoutAuth() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/index.html"));
    }

    @Test
    void apiRoute_stillRequiresAuth() throws Exception {
        mockMvc.perform(get("/api/userTable/admin/allUserList"))
                .andExpect(status().isUnauthorized());
    }
}
