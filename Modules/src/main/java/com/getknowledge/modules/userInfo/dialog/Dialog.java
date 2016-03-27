package com.getknowledge.modules.userInfo.dialog;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.messages.attachments.AttachmentImage;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.dialog.messages.DialogMessage;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dialog")
public class Dialog extends AbstractEntity{

    @ManyToMany
    private List<DialogMessage> messages = new ArrayList<>();

    @OneToMany
    @JsonIgnore
    private List<AttachmentImage> images = new ArrayList<>();

    @ManyToOne
    private UserInfo user;

    @ManyToOne
    private UserInfo companion;

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public UserInfo getCompanion() {
        return companion;
    }

    public void setCompanion(UserInfo companion) {
        this.companion = companion;
    }

    public List<DialogMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<DialogMessage> messages) {
        this.messages = messages;
    }

    public List<AttachmentImage> getImages() {
        return images;
    }

    public void setImages(List<AttachmentImage> images) {
        this.images = images;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
