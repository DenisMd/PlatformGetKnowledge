package com.getknowledge.modules.userInfo.post.messages;

import com.getknowledge.modules.messages.Message;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.*;

@Entity
@Table(name = "post_message")
@ModuleInfo(repositoryName = "PostMessageRepository" , serviceName = "PostMessageService")
public class PostMessage extends Message {

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
