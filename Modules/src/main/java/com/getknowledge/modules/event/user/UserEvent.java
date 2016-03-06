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
    @Column(name = "user_event_type")
    private UserEventType userEventType;

    @Column(columnDefinition = "Text" , name = "data")
    private String data;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    private Calendar createTime;

    @Column(name = "is_checked")
    private boolean isChecked = false;

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
