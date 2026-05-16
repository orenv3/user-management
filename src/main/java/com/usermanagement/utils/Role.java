package com.usermanagement.utils;

public enum Role {
    USER,
    ADMIN;

    public static Role chooseRole(Boolean isAdmin){
        if(isAdmin)
            return ADMIN;
        return USER;
    }
}
