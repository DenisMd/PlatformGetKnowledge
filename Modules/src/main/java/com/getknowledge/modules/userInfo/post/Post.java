package com.getknowledge.modules.userInfo.post;

import com.getknowledge.modules.userInfo.post.message.PostMessage;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post")
@ModuleInfo(repositoryName = "PostRepository", serviceName = "PostService")
public class Post extends AbstractEntity {

    @OneToMany
    private List<PostMessage> messages = new ArrayList<>();

    public List<PostMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<PostMessage> messages) {
        this.messages = messages;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.allowReadEveryOne = true;
        authorizationList.allowCreateEveryOne = false;
        return authorizationList;
    }
}
