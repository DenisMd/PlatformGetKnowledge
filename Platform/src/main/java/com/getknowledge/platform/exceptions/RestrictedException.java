package com.getknowledge.platform.exceptions;

import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.http.HttpStatus;


public class RestrictedException extends PlatformException {

    @Override
    public boolean isSaveToDataBase() {
        return false;
    }


    public RestrictedException(String message, TraceService traceService) {
        super(message, traceService, TraceLevel.Warning);
        super.errorResource.setStatus(HttpStatus.FORBIDDEN);
    }

    public RestrictedException(String message, TraceService traceService, Exception e) {
        super(message, traceService, TraceLevel.Warning, e);
        super.errorResource.setStatus(HttpStatus.FORBIDDEN);
    }
}
