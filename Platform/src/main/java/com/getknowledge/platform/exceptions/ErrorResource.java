package com.getknowledge.platform.exceptions;

import org.springframework.http.HttpStatus;

public class ErrorResource {
    private boolean isError = true;
    private String message;
    private HttpStatus status;

    public boolean isError() {
        return isError;
    }

    public void setIsError(boolean isError) {
        this.isError = isError;
    }

    public ErrorResource(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public ErrorResource() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
