package com.getknowledge.platform.exceptions;

import org.springframework.http.HttpStatus;

public class InvokeException extends PlatformException {
    public InvokeException(String message) {
        super(message);
        super.errorResource.setStatus( HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
