package com.getknowledge.modules.userInfo.results;

public enum RegisterResult {
    UserAlreadyCreated , Complete , RegistrationTimeout;

    private Long userInfoId = null;

    public long getUserInfoId() {
        return userInfoId;
    }

    public void setUserInfoId(long userInfoId) {
        this.userInfoId = userInfoId;
    }
}
