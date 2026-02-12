package com.usermanagement.utils;

public enum Role {
    USER,
    ADMIN;

    public static Role chooseRole(boolean isAdmin){
        if(isAdmin)
            return ADMIN;
        return USER;
    }
}
