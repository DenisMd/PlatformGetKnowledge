package com.getknowledge.modules.userInfo.privacy.mesages;

import com.getknowledge.modules.messages.Message;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "privacy_messages")
@ModuleInfo(repositoryName = "PrivacyMessagesRepository",serviceName = "PrivacyMessagesService")
public class PrivacyMessages extends Message {

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
