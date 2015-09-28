package com.getknowledge.platform.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_EXTENDED)
public class MandatoryFieldNotContainException extends PlatformException {
    public MandatoryFieldNotContainException(String message) {
        super(message);
        super.errorResource.setStatus(HttpStatus.NOT_EXTENDED);
    }
}
