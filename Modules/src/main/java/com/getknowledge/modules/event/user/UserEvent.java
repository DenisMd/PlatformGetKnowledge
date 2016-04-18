package com.getknowledge.modules.event.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Table(name = "user_event")
@ModuleInfo(repositoryName = "UserEventRepository" , serviceName = "UserEventService")
public class UserEvent extends AbstractEntity{

    @OneToOne(optional = false)
    @JsonIgnore
    private UserInfo owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_event_type" , nullable =  false)
    private UserEventType userEventType;

    @Column(columnDefinition = "Text" , name = "data")
    private String data;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time" , nullable = false)
    private Calendar createTime;

    @Column(name = "is_checked" , nullable = false)
    private Boolean checked = false;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Calendar getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Calendar createTime) {
        this.createTime = createTime;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public UserInfo getOwner() {
        return owner;
    }

    public void setOwner(UserInfo owner) {
        this.owner = owner;
    }

    public UserEventType getUserEventType() {
        return userEventType;
    }

    public void setUserEventType(UserEventType userEventType) {
        this.userEventType = userEventType;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.allowReadEveryOne = false;
        authorizationList.allowCreateEveryOne = false;
        if (owner != null)
            authorizationList.getUserList().add(owner.getUser());
        return authorizationList;
    }
}
