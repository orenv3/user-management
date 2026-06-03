package com.usermanagement.controllers;

import com.usermanagement.dao.services.TaskService;
import com.usermanagement.errorHandler.TaskGeneralErrorException;
import com.usermanagement.requestObjects.CreateTaskRequest;
import com.usermanagement.requestObjects.UpdateTaskRequest;
import com.usermanagement.responseObjects.TaskResponse;
import com.usermanagement.responseObjects.TaskTableResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@Validated
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "TaskController", description = "The Task API." +
        " Contains all the operations that can be performed on Task table.")
@RequestMapping("/api/task/")
@RestController
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    /**
     * Admin privilege
     * create Task
     * @param taskObj CreateTaskRequest object with task detail to create
     * @return
     */
    @PostMapping("admin/createTask")
    @Operation(summary = "Create a new task (admin only)")
    public TaskResponse create(@Valid @RequestBody() CreateTaskRequest taskObj) throws TaskGeneralErrorException {
        logRequest("createTask", "title=" + taskObj.title());
        return taskService.createTask(taskObj);
    }

    /**
     * Admin privilege
     * Delete Task by ID
     * @param id task id
     * @return String succeed
     */
    @DeleteMapping("admin/deleteTask/{id}")
    @Operation(summary = "Delete a task by id (admin only)")
    public String delete(@NotNull @PathVariable("id") long id) {
        logRequest("deleteTask", "id=" + id);
        return taskService.deleteTask(id);
    }

    /**
     * Admin privilege
     * Update task
     * @param taskObj UpdateTaskRequest object with task details to update
     * @return
     */
    @PutMapping("admin/updateTask")
    @Operation(summary = "Update a task (admin only)")
    public TaskResponse update(@Valid @RequestBody() UpdateTaskRequest taskObj) throws TaskGeneralErrorException {
        logRequest("updateTask", "id=" + taskObj.id());
        return taskService.updateTask(taskObj);
    }

    /**
     * Admin privilege
     * Get list of all task in the DB
     * @return
     */
    @GetMapping("admin/allTaskList")
    @Operation(summary = "List all tasks (admin only)")
    public List<TaskResponse> getAllTaskList(){
        logRequest("allTaskList", null);
        return taskService.getAllTaskList();
    }

 /**
     * Admin privilege
     * Get list of all task in the DB with PAGINATION
     * @return
     */
    @GetMapping("admin/allTaskListWithPagination")
    public List<TaskResponse> getAllTaskListWithPagination(@NotNull int pageNo, @NotNull int pageSize){
        logRequest("allTaskListWithPagination", "pageNo=" + pageNo + ", pageSize=" + pageSize);
        return taskService.getAllTaskListWithPageRequest(pageNo,pageSize);
    }

    /**
     * Admin privilege
     * Assign task to a user
     * @param taskId task ID for assignation
     * @param userId user ID for assignation
     * @return
     */
    @PutMapping("admin/assignUser{taskId}/{userId}")
    @Operation(summary = "Assign a task to a user (admin only)")
    public TaskTableResponse assignUserToTask(@PathVariable("taskId") long taskId, @PathVariable("userId") long userId){
        logRequest("assignUserToTask", "taskId=" + taskId + ", userId=" + userId);
        return taskService.assignUserToTask(taskId,userId);
    }

    /**
     * Admin privilege
     * Un-assign user from task
     * @param taskId
     * @return
     */
    @PutMapping("admin/removeUserFromTask/{taskId}")
    public TaskTableResponse removeUserFromTask(@PathVariable("taskId") long taskId){
        logRequest("removeUserFromTask", "taskId=" + taskId);
        return taskService.unassignUserFromTask(taskId);
    }

    /**
     * ***********   Below regular user privilege methods   **************
     */

    /**
     * User privilege
     * Get all task of specific user
     * @param assignee user detail
     * @return
     */
    @GetMapping("user/allTaskList/{assignee}")
    @Operation(summary = "List tasks assigned to a user (user)")
    public List<TaskTableResponse> getAllUserTaskList(@PathVariable("assignee") @Min(1) long assignee){
        logRequest("allUserTaskList", "assignee=" + assignee);
        return taskService.getAllUserTaskList(assignee);
    }

    /**
     * User privilege
     * User can update his task status to 'complete'
     * @param taskId
     * @return
     */
    @PutMapping("user/updateComplete")
    @Operation(summary = "Mark a task as completed (user)")
    public String setTaskComplete(Long taskId) {
        logRequest("setTaskComplete", "taskId=" + taskId);
        return taskService.setTaskComplete(taskId);
    }

    private static void logRequest(String action, String details) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String principal = auth == null ? "anonymous" : String.valueOf(auth.getName());
        Object authorities = auth == null ? "[]" : auth.getAuthorities();
        if (details == null || details.isBlank()) {
            log.info("TaskController action={} principal={} authorities={}", action, principal, authorities);
        } else {
            log.info("TaskController action={} principal={} authorities={} details={}", action, principal, authorities, details);
        }
    }

}
