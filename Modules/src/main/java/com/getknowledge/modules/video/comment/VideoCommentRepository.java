package com.getknowledge.modules.video.comment;

import com.getknowledge.modules.messages.CommentStatus;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.video.Video;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.List;

@Repository("VideoCommentRepository")
public class VideoCommentRepository extends ProtectedRepository<VideoComment> {
    @Override
    protected Class<VideoComment> getClassEntity() {
        return VideoComment.class;
    }

    public void createComment(String text, Video video, UserInfo userInfo) {
        VideoComment videoComment = new VideoComment();
        videoComment.setVideo(video);
        videoComment.setCreateTime(Calendar.getInstance());
        videoComment.setMessage(text);
        videoComment.setCommentStatus(CommentStatus.Normal);
        videoComment.setSender(userInfo);
        create(videoComment);
    }

    public void blockComment(VideoComment videoComment,CommentStatus commentStatus){
        videoComment.setMessage(null);
        videoComment.setCommentStatus(commentStatus);
        merge(videoComment);
    }

    public VideoComment lastVideoComment() {
        List<VideoComment> videoCommentList = entityManager.createQuery(
                "select vc from VideoComment vc " +
                        "where vc.createTime = (select max(vc2.createTime) from VideoComment vc2)"
        ).getResultList();

        return videoCommentList.isEmpty() ? null : videoCommentList.get(0);
    }
}
