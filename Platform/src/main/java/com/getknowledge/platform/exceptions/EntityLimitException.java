package com.getknowledge.platform.exceptions;

import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
public class EntityLimitException  extends PlatformException {
    public EntityLimitException(String message) {
        super(message);
        super.errorResource.setStatus(HttpStatus.NOT_FOUND);
    }

    public EntityLimitException(String message, TraceService traceService, TraceLevel traceLevel) {
        super(message, traceService, traceLevel);
        super.errorResource.setStatus(HttpStatus.NOT_FOUND);
    }
}