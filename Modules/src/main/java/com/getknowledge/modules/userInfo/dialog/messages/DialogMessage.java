package com.getknowledge.modules.userInfo.dialog.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.messages.Message;
import com.getknowledge.modules.messages.attachments.AttachmentImage;
import com.getknowledge.modules.userInfo.dialog.Dialog;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dialog_messages")
@ModuleInfo(serviceName = "DialogMessageService")
public class DialogMessage extends Message {

    @ManyToMany(mappedBy = "messages")
    @JsonIgnore
    private List<Dialog> dialogs = new ArrayList<>();

    @OneToMany
    @JsonIgnore
    private List<AttachmentImage> images = new ArrayList<>();

    public List<Dialog> getDialogs() {
        return dialogs;
    }

    public void setDialogs(List<Dialog> dialogs) {
        this.dialogs = dialogs;
    }

    public List<AttachmentImage> getImages() {
        return images;
    }

    public void setImages(List<AttachmentImage> images) {
        this.images = images;
    }

    @Override
    public AbstractEntity clone() {
        DialogMessage dialogMessage = new DialogMessage();
        dialogMessage.setCreateTime(this.getCreateTime());
        dialogMessage.setMessage(this.getMessage());
        dialogMessage.setSender(this.getSender());
        dialogMessage.setId(this.getId());
        dialogMessage.setObjectVersion(this.getObjectVersion());
        return dialogMessage;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
