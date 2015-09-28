package com.getknowledge.platform.controllers;

import com.getknowledge.platform.exceptions.PlatformException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class PlatformExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({PlatformException.class})
    protected ResponseEntity<Object> handleInvalidRequest(PlatformException e, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return handleExceptionInternal(e, e.getErrorResource(), headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }
}
