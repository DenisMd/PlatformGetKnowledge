package com.getknowledge.modules.userInfo.restore.password;

import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Calendar;

@Entity
@Table(name = "restore_password")
@ModuleInfo(repositoryName = "RestorePasswordInfoRepository" , serviceName = "RestorePasswordInfoService")
public class RestorePasswordInfo extends AbstractEntity {

    @OneToOne
    private UserInfo userInfo;

    private String uuid;

    private Calendar calendar;

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
