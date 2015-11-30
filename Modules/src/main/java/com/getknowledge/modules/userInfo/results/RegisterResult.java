package com.getknowledge.modules.userInfo.results;

public enum RegisterResult {
    PasswordLessThan6,UserAlreadyCreated , Complete , RegistrationTimeout,AlreadyActivate,LanguageNotSupported,NotFound;

    private Long userInfoId = null;

    public long getUserInfoId() {
        return userInfoId;
    }

    public void setUserInfoId(long userInfoId) {
        this.userInfoId = userInfoId;
    }
}
