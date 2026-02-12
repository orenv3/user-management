package com.usermanagement.errorHandler;

public class UserValidationErrorException extends Exception {

    public UserValidationErrorException(String message) {
        super(message);
    }
}
