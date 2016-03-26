package com.getknowledge.platform.exceptions;

import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class DeleteException extends PlatformException{
    public DeleteException(String message) {
        super(message);
        super.errorResource.setStatus(HttpStatus.NOT_ACCEPTABLE);
    }

    public DeleteException(String message, TraceService traceService, TraceLevel traceLevel) {
        super(message, traceService, traceLevel);
        super.errorResource.setStatus(HttpStatus.NOT_ACCEPTABLE);
    }
}
