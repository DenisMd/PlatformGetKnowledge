package com.getknowledge.modules.video.comment;

import com.getknowledge.modules.messages.comments.CommentRepository;
import com.getknowledge.modules.messages.comments.CommentService;
import com.getknowledge.modules.messages.comments.CommentStatus;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoRepository;
import com.getknowledge.modules.userInfo.blocker.BlockerTypes;
import com.getknowledge.modules.userInfo.blocker.UserActionFilterService;
import com.getknowledge.modules.video.Video;
import com.getknowledge.modules.video.VideoRepository;
import com.getknowledge.modules.video.VideoService;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.services.AuthorizedService;
import com.getknowledge.platform.exceptions.NotAuthorized;
import com.getknowledge.platform.modules.Result;
import com.getknowledge.platform.modules.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.HashMap;

@Service("VideoCommentService")
public class VideoCommentService extends CommentService<VideoComment> {

    @Autowired
    private VideoCommentRepository videoCommentRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideoService videoService;


    @Override
    protected CommentRepository<VideoComment> getRepository() {
        return videoCommentRepository;
    }

    @Override
    protected Result createComment(Long objectId, String text, UserInfo sender) {

        Video video = videoRepository.read(objectId);

        if (video == null){
            return Result.NotFound();
        }

        if (!videoService.isAccessForRead(sender.getUser(),video)){
            return Result.AccessDenied();
        }

        videoCommentRepository.createComment(text,video,sender);
        return Result.Complete();
    }

    @Override
    public boolean isAccessForRead(User currentUser, VideoComment entity) {
        if (entity == null) return false;
        return videoService.isAccessForRead(currentUser,entity.getVideo());
    }
}
