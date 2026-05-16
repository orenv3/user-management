package com.usermanagement.requestObjects;

import com.usermanagement.utils.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreateTaskRequestTest {

    @Test
    void compactConstructor_defaultsNullStatusToPending() {
        CreateTaskRequest r = new CreateTaskRequest("t1", "desc", null);
        assertThat(r.status()).isEqualTo(new TaskStatus().getPENDING());
    }

    @Test
    void compactConstructor_defaultsBlankStatusToPending() {
        CreateTaskRequest r = new CreateTaskRequest("t1", "desc", "   ");
        assertThat(r.status()).isEqualTo(new TaskStatus().getPENDING());
    }
}
