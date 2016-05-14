package com.getknowledge.platform.exceptions;

import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.http.HttpStatus;


public class RestrictedException extends PlatformException {
    public RestrictedException(String message) {
        super(message);
        super.errorResource.setStatus( HttpStatus.FORBIDDEN);
    }

    public RestrictedException(String message, TraceService traceService, TraceLevel traceLevel) {
        super(message, traceService, traceLevel);
        super.errorResource.setStatus(HttpStatus.FORBIDDEN);
    }

    public RestrictedException(String message, TraceService traceService, TraceLevel traceLevel, Exception e) {
        super(message, traceService, traceLevel, e);
        super.errorResource.setStatus(HttpStatus.FORBIDDEN);
    }
}
