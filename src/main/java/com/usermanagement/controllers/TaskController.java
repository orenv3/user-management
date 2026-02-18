package com.usermanagement.controllers;

import com.usermanagement.services.TaskService;
import com.usermanagement.errorHandler.TaskGeneralErrorException;
import com.usermanagement.requestObjects.CreateTaskRequest;
import com.usermanagement.requestObjects.UpdateTaskRequest;
import com.usermanagement.responseObjects.TaskResponse;
import com.usermanagement.responseObjects.TaskTableResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@Validated
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "TaskController", description = "The Task API." +
        " Contains all the operations that can be performed on Task table.")
@RequestMapping("/api/task/")
@RestController
public class TaskController {

    private final TaskService taskService;
    
    // Explicit constructor to break compilation cycle
    // public TaskController(TaskService taskService) {
    //     this.taskService = taskService;
    // }

    /**
     * Admin privilege
     * create Task
     * @param taskObj CreateTaskRequest object with task detail to create
     * @return
     */
    @PostMapping("admin/createTask")
    public TaskResponse create(@Valid @RequestBody() CreateTaskRequest taskObj) throws TaskGeneralErrorException {
        return taskService.createTask(taskObj);
    }

    /**
     * Admin privilege
     * Delete Task by ID
     * @param id task id
     * @return String succeed
     */
    @DeleteMapping("admin/deleteTask/{id}")
    public String delete(@NotNull @PathVariable("id") long id) {
        return taskService.deleteTask(id);
    }

    /**
     * Admin privilege
     * Update task
     * @param taskObj UpdateTaskRequest object with task details to update
     * @return
     */
    @PutMapping("admin/updateTask")
    public TaskResponse update(@Valid @RequestBody() UpdateTaskRequest taskObj) throws TaskGeneralErrorException {
        return taskService.updateTask(taskObj);
    }

    /**
     * Admin privilege
     * Get list of all task in the DB
     * @return
     */
    @GetMapping("admin/allTaskList")
    public List<TaskResponse> getAllTaskList(){
        return taskService.getAllTaskList();
    }

 /**
     * Admin privilege
     * Get list of all task in the DB with PAGINATION
     * @return
     */
    @GetMapping("admin/allTaskListWithPagination")
    public List<TaskResponse> getAllTaskListWithPagination(@NotNull int pageNo, @NotNull int pageSize){
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
    public TaskTableResponse assignUserToTask(@PathVariable("taskId") long taskId, @PathVariable("userId") long userId){
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
    public List<TaskTableResponse> getAllUserTaskList(@PathVariable("assignee") @Min(1) long assignee){
        return taskService.getAllUserTaskList(assignee);
    }

    /**
     * User privilege
     * User can update his task status to 'complete'
     * @param taskId
     * @return
     */
    @PutMapping("user/updateComplete")
    public String setTaskComplete(Long taskId) {
        return taskService.setTaskComplete(taskId);
    }


}
