package com.getknowledge.platform.exceptions;

import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.LENGTH_REQUIRED)
public class LimitException extends PlatformException {

    @Override
    public boolean isSaveToDataBase() {
        return false;
    }

    public LimitException(String message, TraceService traceService) {
        super(message, traceService, TraceLevel.Warning);
        super.errorResource.setStatus(HttpStatus.LENGTH_REQUIRED);
    }
}
