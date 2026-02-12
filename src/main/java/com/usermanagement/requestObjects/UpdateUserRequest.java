package com.usermanagement.requestObjects;

import com.usermanagement.entities.User;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Optional;


public record UpdateUserRequest(

        @NotNull @Min(2) Long id,
        @Size(max = 15) String name,
        String email,
        boolean isAdmin,
        boolean active,
        String password){

    public User updateUserParameters(UpdateUserRequest updateObj, User user){

        if(updateObj.isName())
            user.setName(updateObj.name());
        if(updateObj.isAdminNotNull())
            user.setAdmin(updateObj.isAdmin());
        if(updateObj.isUserActive())
            user.setActive(updateObj.active());
        if(updateObj.isPassword())
            user.setPassword(updateObj.password());
        if(updateObj.isEmail())
            user.setEmail(updateObj.email());


        return user;
    }

    private boolean isName(){
        if(this.name==null)
            return false;
       return this.name.isEmpty()? false:true;
    }
    private boolean isAdminNotNull(){
       return Optional.of(this.isAdmin).isPresent();
    }
    private boolean isUserActive() {
        return Optional.of(this.active).isPresent();
    }
    private boolean isPassword(){
        if(this.password==null)
            return false;
        return this.password.isBlank()?false:true;

    }

    private boolean isEmail(){
        if(this.email==null)
            return false;
        return this.email.isBlank()?false:true;

    }
}
