package com.usermanagement.dao.services;

import com.usermanagement.entities.Task;
import com.usermanagement.entities.User;
import com.usermanagement.errorHandler.TaskGeneralErrorException;
import com.usermanagement.mappers.EntityMapper;
import com.usermanagement.repositories.TaskRepo;
import com.usermanagement.requestObjects.CreateTaskRequest;
import com.usermanagement.requestObjects.UpdateTaskRequest;
import com.usermanagement.responseObjects.TaskResponse;
import com.usermanagement.responseObjects.TaskTableResponse;
import com.usermanagement.utils.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service("TaskImpl")
public class TaskService {


    private final TaskRepo taskRepo;
    private final UserService userService;
    private final EntityMapper entityMapper;
    private TaskStatus taskStatus = new TaskStatus();
    

    public TaskResponse createTask(CreateTaskRequest taskObj) throws TaskGeneralErrorException {
        TaskStatus taskStatus = new TaskStatus();
        taskObj.gotValidationException(taskStatus);
        Task task = entityMapper.toEntity(taskObj);
        Optional<Task> checkDuplication = taskRepo.findByTitle(task.getTitle());
        if(checkDuplication.isPresent())
            throw new TaskGeneralErrorException("The Task already exists. Can not create task with the same title.");
        Task savedTask =  taskRepo.save(task);
        return entityMapper.toTaskResponse(savedTask);
    }

    public TaskResponse updateTask(UpdateTaskRequest taskObj) throws TaskGeneralErrorException {
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
        return entityMapper.toTaskResponse(savedTask);
    }

    public String deleteTask(long id){
        Task task = taskRepo.getReferenceById(id);
        taskRepo.deleteById(id);
        return "Deleted: "+!(taskRepo.existsById(id));
    }


    public List<TaskResponse> getAllTaskList(){
        List<Task> taskList = taskRepo.findAll();
        return entityMapper.toTaskResponseList(taskList);
    }

    public List<TaskResponse> getAllTaskListWithPageRequest(int pageNo, int pageSize){
        Pageable pageable = PageRequest.of(pageNo,pageSize);
        return entityMapper.toTaskResponseList(taskRepo.findAll(pageable).getContent());
    }

    public List<TaskTableResponse> getAllUserTaskList(long assignee){
        List<Task> taskList = taskRepo.getAllByAssignee(assignee,taskStatus.getARCHIVED());
        return entityMapper.toTaskTableResponseList(taskList);
    }

    public TaskTableResponse assignUserToTask(long taskId, long userId){
        User user = userService.getUserById(userId);
        Task task = taskRepo.getReferenceById(taskId);
        String additionalMessage="";
        if(task.getAssignee() != null){
            additionalMessage = " The Task old assignee was: "+ task.getAssignee();
        }

        task.setAssignee(user);
        task = taskRepo.save(task);
        String err = "The assignation executed successfully: "+task;
        return entityMapper.toTaskTableResponseWithError(task, err + additionalMessage);
    }

    public TaskTableResponse unassignUserFromTask(long taskId){
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
                    response.title(),
                    response.description(),
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
       int check = taskRepo.updateTaskToComplete(taskId,taskStatus.getCOMPLETED());

       if(check==0)
        return "The update did not occurred. ";

       return "Update successfully";
    }
}
