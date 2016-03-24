package com.getknowledge.modules.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.messages.attachments.PostImage;
import com.getknowledge.platform.base.entities.AbstractEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

    @JsonIgnore
    @OneToMany(mappedBy = "postMessage")
    private List<PostImage> images = new ArrayList<>();

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

    public List<PostImage> getImages() {
        return images;
    }

    public void setImages(List<PostImage> images) {
        this.images = images;
    }
}
