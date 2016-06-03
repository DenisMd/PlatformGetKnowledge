package com.getknowledge.platform.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFound extends PlatformException{

    @Override
    public boolean isSaveToDataBase() {
        return false;
    }

    public NotFound(String message) {
        super(message);
        super.errorResource.setStatus(HttpStatus.NOT_FOUND);
    }
}
