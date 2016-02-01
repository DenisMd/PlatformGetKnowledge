package com.getknowledge.modules.userInfo.post.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Table(name = "post_message")
@ModuleInfo(repositoryName = "PostMessageRepository" , serviceName = "PostMessageService")
public class PostMessage extends AbstractEntity {

    @Column(columnDefinition = "Text" , name = "message")
    private String message;

    @Basic(fetch= FetchType.LAZY)
    @Lob @Column(name="image")
    @JsonIgnore
    private byte[] image;

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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
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

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.allowCreateEveryOne = false;
        authorizationList.allowReadEveryOne = true;
        return authorizationList;
    }
}
