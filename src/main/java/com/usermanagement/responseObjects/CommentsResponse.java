package com.usermanagement.responseObjects;

// import com.usermanagement.entities.Task;
// import com.usermanagement.entities.Users;

import java.util.Date;

public record CommentsResponse(

        Date timestamp,

        String comment,

        Long userId,

        Long taskId,

        String title,

        String err

) {


}
