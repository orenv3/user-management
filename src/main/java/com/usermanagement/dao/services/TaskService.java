package com.usermanagement.dao.services;

import com.usermanagement.entities.Task;
import com.usermanagement.entities.Users;
import com.usermanagement.errorHandler.TaskGeneralErrorException;
import com.usermanagement.mappers.EntityMapper;
import com.usermanagement.repositories.TaskRepo;
import com.usermanagement.requestObjects.CreateTaskRequest;
import com.usermanagement.requestObjects.UpdateTaskRequest;
import com.usermanagement.responseObjects.TaskResponse;
import com.usermanagement.responseObjects.TaskTableResponse;
import com.usermanagement.utils.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service("TaskImpl")
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepo taskRepo;
    private final UserService userService;
    private final EntityMapper entityMapper;
    private TaskStatus taskStatus = new TaskStatus();
    

    public TaskResponse createTask(CreateTaskRequest taskObj) throws TaskGeneralErrorException {
        log.info("Create task requested. title={}", taskObj.title());
        TaskStatus taskStatus = new TaskStatus();
        taskObj.gotValidationException(taskStatus);
        Task task = entityMapper.toEntity(taskObj);
        Optional<Task> checkDuplication = taskRepo.findByTitle(task.getTitle());
        if(checkDuplication.isPresent())
            throw new TaskGeneralErrorException("The Task already exists. Can not create task with the same title.");
        Task savedTask =  taskRepo.save(task);
        log.info("Task created. id={} title={}", savedTask.getId(), savedTask.getTitle());
        return entityMapper.toTaskResponse(savedTask);
    }

    public TaskResponse updateTask(UpdateTaskRequest taskObj) throws TaskGeneralErrorException {
        log.info("Update task requested. id={}", taskObj.id());
        Task task = taskRepo.getReferenceById(taskObj.id());
        // Validate status if provided
        if (taskObj.status() != null && !taskObj.status().isBlank()) {
            TaskStatus taskStatus = new TaskStatus();
            if (!taskStatus.isValidStatus(taskObj.status())) {
                throw new TaskGeneralErrorException("The status:" + taskObj.status() +
                        " is not valid. \n Please enter one of the following: " + taskStatus.getStatusOptions());
            }
        }
        entityMapper.updateTaskFromRequest(taskObj, task);
        Task savedTask =  taskRepo.save(task);
        log.info("Task updated. id={} status={}", savedTask.getId(), savedTask.getStatus());
        return entityMapper.toTaskResponse(savedTask);
    }

    public String deleteTask(long id){
        log.info("Delete task requested. id={}", id);
        taskRepo.deleteById(id);
        boolean deleted = !(taskRepo.existsById(id));
        log.info("Delete task result. id={} deleted={}", id, deleted);
        return "Deleted: " + deleted;
    }


    public List<TaskResponse> getAllTaskList(){
        log.info("Get all tasks requested.");
        List<Task> taskList = taskRepo.findAll();
        log.info("Get all tasks result count={}", taskList.size());
        return entityMapper.toTaskResponseList(taskList);
    }

    public List<TaskResponse> getAllTaskListWithPageRequest(int pageNo, int pageSize){
        log.info("Get tasks page requested. pageNo={} pageSize={}", pageNo, pageSize);
        Pageable pageable = PageRequest.of(pageNo,pageSize);
        List<Task> content = taskRepo.findAll(pageable).getContent();
        log.info("Get tasks page result count={}", content.size());
        return entityMapper.toTaskResponseList(content);
    }

    public List<TaskTableResponse> getAllUserTaskList(long assignee){
        log.info("Get user task list requested. assignee={}", assignee);
        List<Task> taskList = taskRepo.getAllByAssignee(assignee,taskStatus.getARCHIVED());
        log.info("Get user task list result count={} assignee={}", taskList.size(), assignee);
        return entityMapper.toTaskTableResponseList(taskList);
    }

    public TaskTableResponse assignUserToTask(long taskId, long userId){
        log.info("Assign user to task requested. taskId={} userId={}", taskId, userId);
        Users user = userService.getUserById(userId);
        Task task = taskRepo.getReferenceById(taskId);
        String additionalMessage="";
        if(task.getAssignee() != null){
            additionalMessage = " The Task old assignee was: "+ task.getAssignee();
        }

        task.setAssignee(user);
        task = taskRepo.save(task);
        String err = "The assignation executed successfully: "+task;
        log.info("Assign user to task succeeded. taskId={} userId={} oldAssigneePresent={}", taskId, userId, !additionalMessage.isBlank());
        return entityMapper.toTaskTableResponseWithError(task, err + additionalMessage);
    }

    public TaskTableResponse unassignUserFromTask(long taskId){
        log.info("Unassign user from task requested. taskId={}", taskId);
        Task task = taskRepo.getReferenceById(taskId);
        String additionalMessage="";
        if(task.getAssignee() != null){
            additionalMessage = " The Task old assignee was: "+ task.getAssignee();

            task.setAssignee(null);
            task = taskRepo.save(task);
        }else {
            additionalMessage = " No User was assign to this task";
        }
        String err = "The user assign successfully: "+task;
        TaskTableResponse response = entityMapper.toTaskTableResponseWithError(task, err + additionalMessage);
        // Handle null assignee case
        if (task.getAssignee() == null) {
            return new TaskTableResponse(
                    response.task_id(),
                    response.task_title(),
                    response.task_description(),
                    response.task_status(),
                    null,
                    response.err());
        }
        return response;
    }

    public Task getTaskById(long id){
       return  taskRepo.getReferenceById(id);
    }

    public String setTaskComplete(Long taskId) {
       log.info("Set task complete requested. taskId={}", taskId);
       int check = taskRepo.updateTaskToComplete(taskId,taskStatus.getCOMPLETED());

       if(check==0)
        return "The update did not occurred. ";

       return "Update successfully";
    }
}
