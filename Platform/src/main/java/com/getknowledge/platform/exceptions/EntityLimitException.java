package com.getknowledge.platform.exceptions;

import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
public class EntityLimitException  extends PlatformException {

    @Override
    public boolean isSaveToDataBase() {
        return false;
    }

    public EntityLimitException(String message, TraceService traceService) {
        super(message, traceService, TraceLevel.Warning);
        super.errorResource.setStatus(HttpStatus.NOT_FOUND);
    }
}