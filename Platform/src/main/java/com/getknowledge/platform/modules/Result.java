package com.getknowledge.platform.modules;

public class Result {

    public static Result Complete(){
        Result result = new Result();
        result.setStatus("Complete");
        return result;
    }

    public static Result Failed(){
        Result result = new Result();
        result.setStatus("Failed");
        return result;
    }

    public static Result SessionFailed(){
        Result result = new Result();
        result.setStatus("SessionFailed");
        return result;
    }

    public static Result EmailNotSend(){
        Result result = new Result();
        result.setStatus("EmailNotSend");
        return result;
    }

    public static Result NotAuthorized(){
        Result result = new Result();
        result.setStatus("NotAuthorized");
        return result;
    }

    public static Result AccessDenied(){
        Result result = new Result();
        result.setStatus("AccessDenied");
        return result;
    }

    public static Result NotFound(){
        Result result = new Result();
        result.setStatus("Not found");
        return result;
    }

    public static Result Complete(String message){
        Result result = new Result();
        result.setStatus("Complete");
        result.setMessage(message);
        return result;
    }

    public static Result Failed(String message){
        Result result = new Result();
        result.setStatus("Failed");
        result.setMessage(message);
        return result;
    }

    public static Result SessionFailed(String message){
        Result result = new Result();
        result.setStatus("SessionFailed");
        result.setMessage(message);
        return result;
    }

    public static Result EmailNotSend(String message){
        Result result = new Result();
        result.setStatus("EmailNotSend");
        result.setMessage(message);
        return result;
    }

    public static Result NotAuthorized(String message){
        Result result = new Result();
        result.setStatus("NotAuthorized");
        result.setMessage(message);
        return result;
    }

    public static Result AccessDenied(String message){
        Result result = new Result();
        result.setStatus("AccessDenied");
        result.setMessage(message);
        return result;
    }

    public static Result NotFound(String message){
        Result result = new Result();
        result.setStatus("Not found");
        result.setMessage(message);
        return result;
    }

    private String status;

    private Object object = null;

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
