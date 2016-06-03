package com.getknowledge.platform.exceptions;

import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class DeleteException extends PlatformException{

    @Override
    public boolean isSaveToDataBase() {
        return true;
    }

    public DeleteException(String message, TraceService traceService) {
        super(message, traceService, TraceLevel.Error);
        super.errorResource.setStatus(HttpStatus.NOT_ACCEPTABLE);
    }
}
