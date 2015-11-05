package com.getknowledge.platform.exceptions;

import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class NotAuthorized extends PlatformException {
    public NotAuthorized(String message) {
        super(message);
        super.errorResource.setStatus( HttpStatus.FORBIDDEN);
    }

    public NotAuthorized(String message, TraceService traceService, TraceLevel traceLevel) {
        super(message, traceService, traceLevel);
        super.errorResource.setStatus(HttpStatus.FORBIDDEN);
    }
}
