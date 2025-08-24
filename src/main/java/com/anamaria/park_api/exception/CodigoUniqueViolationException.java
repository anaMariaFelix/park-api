package com.anamaria.park_api.exception;

public class CodigoUniqueViolationException extends RuntimeException {

    public CodigoUniqueViolationException(String msg) {
        super(msg);
    }
}
