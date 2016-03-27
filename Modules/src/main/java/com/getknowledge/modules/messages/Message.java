package com.getknowledge.modules.messages;

import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;

import javax.persistence.*;
import java.util.Calendar;

@MappedSuperclass
public abstract class Message extends AbstractEntity {

    @Column(columnDefinition = "Text" , name = "message")
    private String message;

    @Column(name = "createTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createTime;

    @OneToOne
    private UserInfo sender;

    @OneToOne
    private UserInfo recipient;

    public UserInfo getRecipient() {
        return recipient;
    }

    public void setRecipient(UserInfo recipient) {
        this.recipient = recipient;
    }

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
}
