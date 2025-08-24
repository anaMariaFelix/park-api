package com.anamaria.park_api.exception;

public class PasswordInvalidException extends RuntimeException{

    public PasswordInvalidException(String msg){
        super(msg);
    }
}
