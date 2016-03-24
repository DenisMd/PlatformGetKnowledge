package com.getknowledge.modules.userInfo.privacy.mesages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.messages.Message;
import com.getknowledge.modules.messages.attachments.AttachmentImage;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "privacy_messages")
@ModuleInfo(repositoryName = "PrivacyMessagesRepository",serviceName = "PrivacyMessagesService")
public class PrivacyMessages extends Message {

    @OneToMany
    @JoinTable(name = "privacy_messages_images")
    @JsonIgnore
    private List<AttachmentImage> images = new ArrayList<>();

    public List<AttachmentImage> getImages() {
        return images;
    }

    public void setImages(List<AttachmentImage> images) {
        this.images = images;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.allowCreateEveryOne = false;
        authorizationList.allowReadEveryOne = false;
        if (getRecipient() != null)
            authorizationList.getUserList().add(getRecipient().getUser());
        if (getSender() != null)
            authorizationList.getUserList().add(getSender().getUser());
        return authorizationList;
    }
}
