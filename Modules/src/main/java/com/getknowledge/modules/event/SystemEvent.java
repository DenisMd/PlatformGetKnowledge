package com.getknowledge.modules.event;

import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Table(name = "system_event")
@ModuleInfo(repositoryName = "SystemEventRepository" , serviceName = "SystemEventService")
public class SystemEvent extends AbstractEntity {

    @Enumerated(EnumType.STRING)
    SystemEventType systemEventType;

    @OneToOne
    private UserInfo userInfo;

    private String uuid;

    private Calendar calendar;

    public SystemEventType getSystemEventType() {
        return systemEventType;
    }

    public void setSystemEventType(SystemEventType systemEventType) {
        this.systemEventType = systemEventType;
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
