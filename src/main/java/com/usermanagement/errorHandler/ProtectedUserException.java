package com.usermanagement.errorHandler;

public class ProtectedUserException extends RuntimeException {

    public ProtectedUserException(String message) {
        super(message);
    }
}
