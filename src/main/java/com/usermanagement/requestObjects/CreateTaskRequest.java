package com.usermanagement.requestObjects;

import com.usermanagement.errorHandler.TaskGeneralErrorException;
import com.usermanagement.utils.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTaskRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 15, message = "Title must be at most 15 characters")
        String title,
        @Size(max = 40, message = "Description must be at most 40 characters")
        String description,
        String status
) {


    public CreateTaskRequest {
        TaskStatus taskStatus = new TaskStatus();
        if(status == null || status.isBlank())
            status = taskStatus.getPENDING();


    }

    public void gotValidationException( TaskStatus taskStatus) throws TaskGeneralErrorException {
        if (!(taskStatus.isValidStatus(status))) {
            throw new TaskGeneralErrorException("The status:" + status +
                    " is not valid. \n Please enter one of the following: " + taskStatus.getStatusOptions());
        }
    }
}
