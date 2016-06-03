package com.getknowledge.platform.exceptions;

import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.http.HttpStatus;

public class InvokeException extends PlatformException {

    @Override
    public boolean isSaveToDataBase() {
        return false;
    }

    public InvokeException(String message) {
        super(message);
        super.errorResource.setStatus( HttpStatus.UNPROCESSABLE_ENTITY);
    }

    public InvokeException(String message, TraceService traceService, Exception e) {
        super(message, traceService, TraceLevel.Error, e);
    }
}
