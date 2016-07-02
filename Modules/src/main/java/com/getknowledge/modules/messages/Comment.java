package com.getknowledge.modules.messages;

import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.permission.names.PermissionNames;

import javax.persistence.*;
import java.util.Calendar;

@MappedSuperclass
@ModuleInfo(repositoryName = "CommentRepository" , serviceName = "CommentService")
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

    protected  abstract Comment createComment();

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.allowReadEveryOne = true;
        authorizationList.getPermissionsForEdit().add(new Permission(PermissionNames.BlockComments));
        return authorizationList;
    }

    @Override
    public AbstractEntity clone() {
        Comment clone = createComment();
        clone.setCommentStatus(this.getCommentStatus());
        clone.setCreateTime(this.getCreateTime());
        clone.setMessage(this.getMessage());
        clone.setSender(this.getSender());
        clone.setId(this.getId());
        clone.setObjectVersion(this.getObjectVersion());
        return clone;
    }
}
