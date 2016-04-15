package com.getknowledge.modules.messages;

import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.platform.annotations.ModelView;
import com.getknowledge.platform.annotations.ViewType;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.CloneableEntity;

import javax.persistence.*;
import java.util.Calendar;

@MappedSuperclass
public abstract class Message extends AbstractEntity implements CloneableEntity {

    @Column(columnDefinition = "Text" , name = "message")
    private String message;

    @Column(name = "createTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createTime;

    @ManyToOne
    @ModelView(type = ViewType.CompactPublic)
    private UserInfo sender;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserInfo getSender() {
        return sender;
    }

    public void setSender(UserInfo sender) {
        this.sender = sender;
    }

    public Calendar getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Calendar createTime) {
        this.createTime = createTime;
    }

    @Override
    public abstract AbstractEntity clone();
}
