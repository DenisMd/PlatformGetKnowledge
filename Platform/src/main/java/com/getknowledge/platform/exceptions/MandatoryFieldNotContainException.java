package com.getknowledge.platform.exceptions;

import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_EXTENDED)
public class MandatoryFieldNotContainException extends PlatformException {
    public MandatoryFieldNotContainException(String message) {
        super(message);
        super.errorResource.setStatus(HttpStatus.NOT_EXTENDED);
    }

    public MandatoryFieldNotContainException(String message, TraceService traceService, TraceLevel traceLevel) {
        super(message, traceService, traceLevel);
        super.errorResource.setStatus(HttpStatus.NOT_EXTENDED);
    }
}
