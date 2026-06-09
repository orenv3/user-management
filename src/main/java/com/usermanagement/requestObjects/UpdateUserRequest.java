package com.usermanagement.requestObjects;

import com.usermanagement.entities.Users;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Optional;


public record UpdateUserRequest(

        @NotNull(message = "User id is required")
        @Min(value = 2, message = "User id must be at least 2 (protected accounts cannot be edited)")
        Long id,
        @Size(max = 15, message = "Name must be at most 15 characters")
        String name,
        @Email(message = "Email must be a valid address (e.g. user@example.com)")
        String email,
        Boolean isAdmin,
        Boolean active,
        String password){

    public Users updateUserParameters(UpdateUserRequest updateObj, Users user){

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
