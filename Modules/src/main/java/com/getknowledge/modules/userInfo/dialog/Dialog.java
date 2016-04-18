package com.getknowledge.modules.userInfo.dialog;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.messages.attachments.AttachmentImage;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.dialog.messages.DialogMessage;
import com.getknowledge.platform.annotations.ActionWithFile;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dialog")
@ModuleInfo(serviceName = "DialogService")
public class Dialog extends AbstractEntity{

    @ManyToMany
    @JsonIgnore
    private List<DialogMessage> messages = new ArrayList<>();

    @ManyToOne(optional = false)
    private UserInfo user;

    @ManyToOne(optional = false)
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

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
