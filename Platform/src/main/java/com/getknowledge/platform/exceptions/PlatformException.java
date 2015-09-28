package com.getknowledge.platform.exceptions;

public class PlatformException extends Exception {

    ErrorResource errorResource = new ErrorResource();

    public ErrorResource getErrorResource() {
        return errorResource;
    }

    public PlatformException(String message) {
        super(message);
        errorResource.setMessage(message);
    }
}
