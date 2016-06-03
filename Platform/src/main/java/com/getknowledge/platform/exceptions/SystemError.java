package com.getknowledge.platform.exceptions;

import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class SystemError extends PlatformException {

    @Override
    public boolean isSaveToDataBase() {
        return true;
    }

    public SystemError(String message, TraceService traceService,Exception cause) {
        super(message, traceService, TraceLevel.Error,cause);
        super.errorResource.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
