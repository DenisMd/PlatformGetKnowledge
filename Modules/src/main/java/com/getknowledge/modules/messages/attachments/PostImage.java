package com.getknowledge.modules.messages.attachments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.userInfo.post.messages.PostMessage;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.*;

@Entity
@Table(name = "post_image")
@ModuleInfo(repositoryName = "PostImageRepository")
public class PostImage extends AbstractEntity {

    @Basic(fetch= FetchType.LAZY)
    @Lob
    @Column(name="image")
    @JsonIgnore
    private byte[] image;

    @ManyToOne
    @JsonIgnore
    private PostMessage postMessage;

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public PostMessage getPostMessage() {
        return postMessage;
    }

    public void setPostMessage(PostMessage postMessage) {
        this.postMessage = postMessage;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
