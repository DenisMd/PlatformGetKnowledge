package com.getknowledge.platform.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ModuleNotFound extends PlatformException {
    public ModuleNotFound(String message) {
        super(message);
        super.errorResource.setStatus(HttpStatus.NOT_FOUND);
    }
}
