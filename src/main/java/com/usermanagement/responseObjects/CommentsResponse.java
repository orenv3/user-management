package com.usermanagement.responseObjects;

import com.usermanagement.entities.Task;
import com.usermanagement.entities.User;

import java.util.Date;

public record CommentsResponse(

        Date timestamp,

        String comment,

        long userId,

        long taskId,

        String title,

        String err

) {


}
