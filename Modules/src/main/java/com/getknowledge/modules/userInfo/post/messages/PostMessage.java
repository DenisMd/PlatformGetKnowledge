package com.getknowledge.modules.userInfo.post.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.messages.Message;
import com.getknowledge.modules.messages.attachments.AttachmentImage;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import javafx.geometry.Pos;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post_messages")
@ModuleInfo(serviceName = "PostMessageService")
public class PostMessage extends Message {

    @ManyToOne
    @JsonIgnore
    private UserInfo recipient;

    @OneToMany
    @JsonIgnore
    private List<AttachmentImage> images = new ArrayList<>();

    private Boolean comment = false;

    @OneToMany(mappedBy = "basePost")
    @JsonIgnore
    private List<PostMessage> comments = new ArrayList<>();

    @ManyToOne
    @JsonIgnore
    private PostMessage basePost;

    public List<PostMessage> getComments() {
        return comments;
    }

    public void setComments(List<PostMessage> comments) {
        this.comments = comments;
    }

    public PostMessage getBasePost() {
        return basePost;
    }

    public void setBasePost(PostMessage basePost) {
        this.basePost = basePost;
    }

    public boolean isComment() {
        return comment;
    }

    public void setComment(boolean comment) {
        this.comment = comment;
    }

    public UserInfo getRecipient() {
        return recipient;
    }

    public void setRecipient(UserInfo recipient) {
        this.recipient = recipient;
    }

    public List<AttachmentImage> getImages() {
        return images;
    }

    public void setImages(List<AttachmentImage> images) {
        this.images = images;
    }


    @Override
    public AbstractEntity clone() {
        PostMessage postMessage = new PostMessage();
        postMessage.setComment(this.isComment());
        postMessage.setCreateTime(this.getCreateTime());
        postMessage.setMessage(this.getMessage());
        postMessage.setSender(this.getSender());
        postMessage.setId(this.getId());
        postMessage.setObjectVersion(this.getObjectVersion());
        return postMessage;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
