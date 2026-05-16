package com.usermanagement.responseObjects;

public record TaskTableResponse(

        Long task_id,
        String task_title,
        String task_description,
        String task_status, //(pending/completed/archived)
        Long task_assignee,
        String err
) {
}
