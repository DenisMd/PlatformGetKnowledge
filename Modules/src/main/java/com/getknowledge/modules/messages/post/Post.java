package com.getknowledge.modules.messages.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.messages.Message;
import com.getknowledge.modules.messages.attachments.AttachmentImage;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
public abstract class Post<T extends Post> extends Message {

    @OneToMany
    @JsonIgnore
    private List<AttachmentImage> images = new ArrayList<>();

    private Boolean comment = false;

    @OneToMany(mappedBy = "basePost")
    @JsonIgnore
    private List<T> comments = new ArrayList<>();

    @ManyToOne
    @JsonIgnore
    private T basePost;

    public List<AttachmentImage> getImages() {
        return images;
    }

    public void setImages(List<AttachmentImage> images) {
        this.images = images;
    }

    public Boolean isComment() {
        return comment;
    }

    public void setComment(Boolean comment) {
        this.comment = comment;
    }

    public List<T> getComments() {
        return comments;
    }

    public void setComments(List<T> comments) {
        this.comments = comments;
    }

    public T getBasePost() {
        return basePost;
    }

    public void setBasePost(T baseQuestion) {
        this.basePost = baseQuestion;
    }

    public abstract Post createNewPost();

    @Override
    public AbstractEntity clone() {
        Post postMessage = createNewPost();
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
