package com.usermanagement.dao.services;

import com.usermanagement.entities.Task;
import com.usermanagement.entities.User;
import com.usermanagement.errorHandler.TaskGeneralErrorException;
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
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service("TaskImpl")
public class TaskService {


    private final TaskRepo taskRepo;
    private final UserService userService;
    private TaskStatus taskStatus = new TaskStatus();

    public TaskResponse createTask(CreateTaskRequest taskObj) throws TaskGeneralErrorException {
        TaskStatus taskStatus = new TaskStatus();
        taskObj.gotValidationException(taskStatus);
        Task task = new Task(taskObj);
        Optional<Task> checkDuplication = taskRepo.findByTitle(task.getTitle());
        if(checkDuplication.isPresent())
            throw new TaskGeneralErrorException("The Task already exists. Can not create task with the same title.");
        Task savedTask =  taskRepo.save(task);
        return mapToTaskResponse(savedTask);
    }

    public TaskResponse updateTask(UpdateTaskRequest taskObj) throws TaskGeneralErrorException {
        Task task = taskRepo.getReferenceById(taskObj.id());
        task = taskObj.updateTaskParameters(taskObj,task);
        Task savedTask =  taskRepo.save(task);
        return mapToTaskResponse(savedTask);
    }

    public String deleteTask(long id){
        Task task = taskRepo.getReferenceById(id);
        taskRepo.deleteById(id);
        return "Deleted: "+!(taskRepo.existsById(id));
    }


    public List<TaskResponse> getAllTaskList(){
        List<Task> taskList = taskRepo.findAll();
        return taskList.stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getAllTaskListWithPageRequest(int pageNo, int pageSize){
        Pageable pageable = PageRequest.of(pageNo,pageSize);
        return  taskRepo.findAll(pageable)
                .getContent()
                .stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    public List<TaskTableResponse> getAllUserTaskList(long assignee){
        List<Task> taskList = taskRepo.getAllByAssignee(assignee,taskStatus.getARCHIVED());
        List<TaskTableResponse> response = taskList.stream()
                .parallel().map(tsk -> new TaskTableResponse(
                                tsk.getId(),
                                tsk.getTitle(),
                                tsk.getDescription(),
                                tsk.getStatus(),
                                tsk.getAssignee().getId(),
                                ""
                        )).collect(Collectors.toList());

        return response;
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
        TaskTableResponse response = new TaskTableResponse(
                task.getId(),task.getTitle(),
                task.getDescription(),task.getStatus(),
                task.getAssignee().getId(), err + additionalMessage);

        return response;
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
        TaskTableResponse response = new TaskTableResponse(
                task.getId(),task.getTitle(),
                task.getDescription(),task.getStatus(),
                null, err + additionalMessage);

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

    private TaskResponse mapToTaskResponse(Task task) {
        if (task == null) {
            return null;
        }
        Long assigneeId = task.getAssignee() != null ? task.getAssignee().getId() : null;
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                assigneeId
        );
    }
}
