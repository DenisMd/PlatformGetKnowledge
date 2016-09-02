package com.getknowledge.modules.userInfo.post.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.messages.post.Post;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.*;

@Entity
@Table(name = "post_messages")
@ModuleInfo(repositoryName = "PostMessageRepository", serviceName = "PostMessageService")
public class PostMessage extends Post<PostMessage> {

    @ManyToOne
    @JsonIgnore
    private UserInfo recipient;

    public UserInfo getRecipient() {
        return recipient;
    }

    public void setRecipient(UserInfo recipient) {
        this.recipient = recipient;
    }

    @Override
    public Post createNewPost() {
        return new PostMessage();
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
