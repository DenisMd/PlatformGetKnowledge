package com.getknowledge.platform.exceptions;

import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class AccessDeniedException extends PlatformException {

    @Override
    public boolean isSaveToDataBase() {
        return false;
    }

    public AccessDeniedException(String message, TraceService traceService) {
        super(message, traceService, TraceLevel.Warning);
        super.errorResource.setStatus(HttpStatus.FORBIDDEN);
    }
}
