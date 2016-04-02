package com.getknowledge.modules.email;

public enum EmailTemplates {
    ForgotPassword("forgotPassword"),Register("register");

    EmailTemplates(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
