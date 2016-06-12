package com.gvan.exception;

/**
 * Created by ivan on 6/12/16.
 */
public class InvalidDataBlockException extends IllegalArgumentException {
    String message = null;
    public InvalidDataBlockException(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
