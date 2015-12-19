package com.getknowledge.modules.event;

import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Table(name = "user_event")
@ModuleInfo(repositoryName = "UserEventRepository" , serviceName = "UserEventService")
public class UserEvent extends AbstractEntity {

    @Enumerated(EnumType.STRING)
    UserEventType userEventType;

    @OneToOne
    private UserInfo userInfo;

    private String uuid;

    private Calendar calendar;

    public UserEventType getUserEventType() {
        return userEventType;
    }

    public void setUserEventType(UserEventType userEventType) {
        this.userEventType = userEventType;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
