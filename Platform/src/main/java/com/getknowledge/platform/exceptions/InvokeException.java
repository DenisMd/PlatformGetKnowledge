package com.getknowledge.platform.exceptions;

import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.http.HttpStatus;

public class InvokeException extends PlatformException {
    public InvokeException(String message) {
        super(message);
        super.errorResource.setStatus( HttpStatus.UNPROCESSABLE_ENTITY);
    }

    public InvokeException(String message, TraceService traceService, TraceLevel traceLevel) {
        super(message, traceService, traceLevel);
        super.errorResource.setStatus(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    public InvokeException(String message, TraceService traceService, TraceLevel traceLevel, Exception e) {
        super(message, traceService, traceLevel, e);
    }
}
