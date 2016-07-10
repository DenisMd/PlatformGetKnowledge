package com.getknowledge.platform.exceptions;

import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class PersistenceException extends PlatformException  {
    @Override
    public boolean isSaveToDataBase() {
        return true;
    }

    public PersistenceException(String message, TraceService traceService, TraceLevel traceLevel, Exception e) {
        super(message, traceService, traceLevel, e);
        super.errorResource.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
