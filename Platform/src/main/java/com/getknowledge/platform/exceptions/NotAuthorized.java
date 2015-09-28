package com.getknowledge.platform.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class NotAuthorized extends PlatformException {
    public NotAuthorized(String message) {
        super(message);
        super.errorResource.setStatus( HttpStatus.FORBIDDEN);
    }
}
