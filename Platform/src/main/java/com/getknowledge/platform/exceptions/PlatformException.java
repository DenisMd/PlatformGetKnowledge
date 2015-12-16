package com.getknowledge.platform.exceptions;

import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;

public class PlatformException extends Exception {

    ErrorResource errorResource = new ErrorResource();

    public ErrorResource getErrorResource() {
        return errorResource;
    }

    public PlatformException(String message) {
        super(message);
        errorResource.setMessage(message);
    }

    public PlatformException(String message , TraceService traceService , TraceLevel traceLevel) {
        super(message);
        traceService.logException(message, this, traceLevel);
        errorResource.setMessage(message);
    }

    public PlatformException(String message , TraceService traceService , TraceLevel traceLevel , Exception e) {
        super(message);
        traceService.logException(message ,e, traceLevel);
        errorResource.setMessage(message);
    }
}
