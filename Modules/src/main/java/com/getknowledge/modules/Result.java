package com.getknowledge.modules;

public enum Result {
    Complete, Failed, SessionFailed,EmailNotSend,NotAuthorized,AccessDenied;

    private Object object = new Object();

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}