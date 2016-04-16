package com.getknowledge.modules.messages;

import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;

import javax.persistence.*;
import java.util.Calendar;

@MappedSuperclass
public abstract class Comment extends Message {
    @Enumerated(EnumType.STRING)
    @Column(name = "status",nullable = false)
    private CommentStatus commentStatus = CommentStatus.Normal;

    public CommentStatus getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(CommentStatus commentStatus) {
        this.commentStatus = commentStatus;
    }
}
