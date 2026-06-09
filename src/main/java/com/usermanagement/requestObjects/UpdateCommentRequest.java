package com.usermanagement.requestObjects;

import com.usermanagement.entities.Comment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;


public record UpdateCommentRequest(
        @NotNull(message = "Comment id is required")
        Long id,
        @NotBlank(message = "Comment is required")
        @Size(max = 120, message = "Comment must be at most 120 characters")
        String comment){

    public Comment updateCommentParameters(UpdateCommentRequest updateObj, Comment comment){

        if(updateObj.isComment()) {
            comment.setComment(updateObj.comment());
            comment.setTimestamp(new Date());
        }
        return comment;
    }

    private boolean isComment(){
        if(this.comment==null)
            return false;
       return this.comment.isBlank()? false:true;
    }


}
