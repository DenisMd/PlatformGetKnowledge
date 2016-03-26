package com.getknowledge.modules.video.comment;

import com.getknowledge.modules.messages.MessageStatus;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.video.Video;
import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Calendar;

@Repository("VideoCommentRepository")
public class VideoCommentRepository extends BaseRepository<VideoComment> {
    @Override
    protected Class<VideoComment> getClassEntity() {
        return VideoComment.class;
    }

    public void createComment(String text, Video video, UserInfo userInfo) {
        VideoComment videoComment = new VideoComment();
        videoComment.setVideo(video);
        videoComment.setCreateTime(Calendar.getInstance());
        videoComment.setMessage(text);
        videoComment.setMessageStatus(MessageStatus.Normal);
        videoComment.setSender(userInfo);
        create(videoComment);
    }

    public void blockComment(VideoComment videoComment,MessageStatus messageStatus){
        videoComment.setMessage(null);
        videoComment.setMessageStatus(messageStatus);
        merge(videoComment);
    }
}
