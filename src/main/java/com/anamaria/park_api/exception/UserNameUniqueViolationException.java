package com.anamaria.park_api.exception;

public class UserNameUniqueViolationException extends RuntimeException {

    public UserNameUniqueViolationException(String msg) {
        super(msg);
    }
}
