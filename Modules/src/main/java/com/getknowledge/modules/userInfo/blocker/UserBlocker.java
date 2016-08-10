package com.getknowledge.modules.userInfo.blocker;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Table(name = "user_blocker")
@ModuleInfo(repositoryName = "UserBlockerRepository" , serviceName = "UserBlockerService")
public class UserBlocker extends AbstractEntity {

    @Enumerated(EnumType.STRING)
    private BlockerTypes blockerTypes;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar expDate;

    @Column(length = 500)
    private String comment;

    @ManyToOne(optional = false)
    @JsonIgnore
    private UserInfo userInfo;

    public BlockerTypes getBlockerTypes() {
        return blockerTypes;
    }

    public void setBlockerTypes(BlockerTypes blockerTypes) {
        this.blockerTypes = blockerTypes;
    }

    public Calendar getExpDate() {
        return expDate;
    }

    public void setExpDate(Calendar expDate) {
        this.expDate = expDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
