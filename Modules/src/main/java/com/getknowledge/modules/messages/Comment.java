package com.getknowledge.modules.messages;

import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;

import javax.persistence.*;
import java.util.Calendar;

@MappedSuperclass
public abstract class Comment extends AbstractEntity {
    @Column(columnDefinition = "Text" , name = "message")
    private String message;

    @Column(name = "createTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createTime;

    @OneToOne
    private UserInfo sender;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MessageStatus messageStatus;

    public MessageStatus getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(MessageStatus messageStatus) {
        this.messageStatus = messageStatus;
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
