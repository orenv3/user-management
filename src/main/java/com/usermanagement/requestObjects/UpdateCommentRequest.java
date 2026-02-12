package com.usermanagement.requestObjects;

import com.usermanagement.entities.Comment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;


public record UpdateCommentRequest(
        @NotNull Long id,
        @NotBlank @Size(max=120) String comment){

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
