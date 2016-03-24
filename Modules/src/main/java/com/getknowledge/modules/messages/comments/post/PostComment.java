package com.getknowledge.modules.messages.comments.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.messages.Comment;
import com.getknowledge.modules.messages.Message;
import com.getknowledge.modules.userInfo.post.messages.PostMessage;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "post_commnet")
public class PostComment extends Comment {

    @ManyToOne
    @JsonIgnore
    private PostMessage post;

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
