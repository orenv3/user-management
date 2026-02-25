package com.usermanagement.responseObjects;

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
