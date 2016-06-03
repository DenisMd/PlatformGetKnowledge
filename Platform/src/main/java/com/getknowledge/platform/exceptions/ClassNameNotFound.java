package com.getknowledge.platform.exceptions;

import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ClassNameNotFound extends PlatformException {

    @Override
    public boolean isSaveToDataBase() {
        return false;
    }

    public ClassNameNotFound(String message, TraceService traceService) {
        super(String.format("Classname \"%s\" not found",message), traceService, TraceLevel.Warning);
        super.errorResource.setStatus(HttpStatus.NOT_FOUND);
    }
}
