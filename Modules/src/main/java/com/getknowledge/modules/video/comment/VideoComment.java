package com.getknowledge.modules.video.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.messages.Comment;
import com.getknowledge.modules.video.Video;
import com.getknowledge.platform.annotations.ModuleInfo;

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
    protected Comment createComment() {
        return new VideoComment();
    }
}
