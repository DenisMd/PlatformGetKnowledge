package com.getknowledge.modules.video.comment;

import com.getknowledge.modules.messages.CommentRepository;
import com.getknowledge.modules.messages.CommentStatus;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.video.Video;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.List;

@Repository("VideoCommentRepository")
public class VideoCommentRepository extends CommentRepository<VideoComment> {
    @Override
    protected Class<VideoComment> getClassEntity() {
        return VideoComment.class;
    }

    @Override
    protected String getEntityName() {
        return VideoComment.class.getSimpleName();
    }

    public void createComment(String text, Video video, UserInfo userInfo) {
        VideoComment videoComment = new VideoComment();
        videoComment.setVideo(video);
        videoComment.setMessage(text);
        videoComment.setSender(userInfo);
        create(videoComment);
    }
}
