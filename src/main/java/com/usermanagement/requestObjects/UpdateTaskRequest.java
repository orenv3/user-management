package com.usermanagement.requestObjects;

import com.usermanagement.entities.Task;
import com.usermanagement.errorHandler.TaskGeneralErrorException;
import com.usermanagement.utils.TaskStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record UpdateTaskRequest(

        @NotNull Long id,
        @Size(max=15) String title,
        @Size(max=40) String description,
        String status //(pending/completed/archived)

){

    public Task updateTaskParameters(UpdateTaskRequest updateObj, Task task) throws TaskGeneralErrorException {

        if(updateObj.isTitle())
            task.setTitle(updateObj.title());
        if(updateObj.isDescription())
            task.setDescription(updateObj.description());
        if(updateObj.isStatus()){
            TaskStatus taskStatus = new TaskStatus();
            if (!(taskStatus.isValidStatus(status)))
                throw new TaskGeneralErrorException("The status:" + status +
                        " is not valid. \n Please enter one of the following: " + taskStatus.getStatusOptions());
            task.setStatus(updateObj.status());
        }


        return task;
    }

    private boolean isTitle(){
        if(this.title==null)
            return false;
       return this.title.isBlank()? false:true;
    }

    private boolean isDescription(){
        if(this.description==null)
            return false;
        return this.description.isBlank()?false:true;

    }

    private boolean isStatus(){
        if(this.status==null)
            return false;
        return this.status.isBlank()?false:true;

    }
}
