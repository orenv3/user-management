package com.usermanagement.requestObjects;

import com.usermanagement.entities.Task;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateTaskRequestTest {

    @Test
    void updateTaskParameters_blankStatus_doesNotChangeStatus() throws Exception {
        Task task = new Task(new CreateTaskRequest("x", "d", "PENDING"));
        task.setStatus("PENDING");
        UpdateTaskRequest req = new UpdateTaskRequest(task.getId(), null, null, "   ");
        req.updateTaskParameters(req, task);
        assertThat(task.getStatus()).isEqualTo("PENDING");
    }

    @Test
    void updateTaskParameters_nullStatus_doesNotChangeStatus() throws Exception {
        Task task = new Task(new CreateTaskRequest("x", "d", "COMPLETED"));
        task.setStatus("COMPLETED");
        UpdateTaskRequest req = new UpdateTaskRequest(task.getId(), null, null, null);
        req.updateTaskParameters(req, task);
        assertThat(task.getStatus()).isEqualTo("COMPLETED");
    }
}
