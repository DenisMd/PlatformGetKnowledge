package com.getknowledge.platform.exceptions;

import org.springframework.http.HttpStatus;

public class ParseException extends PlatformException {
    public ParseException(String message) {
        super(message);
        super.errorResource.setStatus( HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
