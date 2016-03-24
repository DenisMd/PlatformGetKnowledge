package com.getknowledge.modules.userInfo.post.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.messages.Message;
import com.getknowledge.modules.messages.attachments.AttachmentImage;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post_message")
@ModuleInfo(repositoryName = "PostMessageRepository" , serviceName = "PostMessageService")
public class PostMessage extends Message {


    @OneToMany
    @JoinTable(name = "post_messages_images")
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
        authorizationList.allowReadEveryOne = true;
        if (getRecipient() != null)
            authorizationList.getUserList().add(getRecipient().getUser());
        if (getSender() != null)
            authorizationList.getUserList().add(getSender().getUser());
        return authorizationList;
    }
}
