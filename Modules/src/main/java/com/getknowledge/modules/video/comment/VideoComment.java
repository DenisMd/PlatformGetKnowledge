package com.getknowledge.modules.video.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.messages.Comment;
import com.getknowledge.modules.video.Video;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.base.entities.CloneableEntity;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.permission.names.PermissionNames;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "video_comment")
@ModuleInfo(repositoryName = "VideoCommentRepository" , serviceName = "VideoCommentService")
public class VideoComment extends Comment {

    @ManyToOne(optional = false)
    @JsonIgnore
    private Video video;

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.getPermissionsForEdit().add(new Permission(PermissionNames.BlockComments));
        return authorizationList;
    }

    @Override
    public AbstractEntity clone() {
        VideoComment videoComment = new VideoComment();
        videoComment.setCommentStatus(this.getCommentStatus());
        videoComment.setCreateTime(this.getCreateTime());
        videoComment.setMessage(this.getMessage());
        videoComment.setSender(this.getSender());
        videoComment.setId(this.getId());
        videoComment.setObjectVersion(this.getObjectVersion());
        return videoComment;
    }
}
