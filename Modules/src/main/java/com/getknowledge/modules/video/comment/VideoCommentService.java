package com.getknowledge.modules.video.comment;

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
public class VideoCommentService extends AuthorizedService<VideoComment> {

    @Autowired
    private VideoCommentRepository videoCommentRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideoService videoService;

    @Autowired
    private UserActionFilterService userActionFilterService;

    @Action(name = "blockComment", mandatoryFields = {"commentId","status"})
    @Transactional
    public Result blockComment(HashMap<String,Object> data){

        Long commentId = longFromField("commentId" , data);
        VideoComment videoComment = videoCommentRepository.read(commentId);

        if (videoComment == null)
            return Result.NotFound();

        if (!isAccessToEdit(data,videoComment)){
            return Result.AccessDenied();
        }

        CommentStatus commentStatus = CommentStatus.valueOf((String) data.get("status"));
        if (commentStatus == CommentStatus.Normal) {
            return Result.Failed();
        }
        videoCommentRepository.blockComment(videoComment, commentStatus);

        return Result.Complete();
    }

    @Action(name = "removeComment" , mandatoryFields = {"commentId"})
    @Transactional
    public Result removeComment(HashMap<String,Object> data) throws NotAuthorized {
        UserInfo userInfo = userInfoRepository.getCurrentUser(data);
        if (userInfo == null) {
            return Result.NotAuthorized();
        }

        Long videoCommentId = longFromField("commentId",data);
        VideoComment videoComment = videoCommentRepository.read(videoCommentId);
        if (videoComment == null) {
            return Result.NotFound();
        }

        if (!videoComment.getSender().getId().equals(userInfo.getId())) {
            return Result.AccessDenied();
        }

        videoCommentRepository.remove(videoComment);

        return Result.Complete();
    }

    @Action(name = "addComment" , mandatoryFields = {"objectId","text"})
    @Transactional
    public Result addComment(HashMap<String,Object> data) throws NotAuthorized {
        UserInfo userInfo = userInfoRepository.getCurrentUser(data);
        if (userInfo == null) {
            return Result.NotAuthorized();
        }

        Long videoId = longFromField("objectId",data);
        Video video = videoRepository.read(videoId);

        if (video == null){
            return Result.NotFound();
        }

        if (!videoService.isAccessForRead(userInfo.getUser(),video)){
            return Result.AccessDenied();
        }

        if (!userActionFilterService.filterAction(userInfo, (String) data.get("ipAddress"), BlockerTypes.GeneralComments)) {
            return Result.AccessDenied();
        }

        String text = (String) data.get("text");
        if (text.length() > 250) {
            return Result.Failed("max_length");
        }

        VideoComment last = videoCommentRepository.getLastComment();

        //Если один и тот же пользователь в течении 30 сек пытается отправить еще одно сообщение отклоняем его как спам
        if (last!= null && last.getSender().getId().equals(userInfo.getId())) {

            long subtract =  Calendar.getInstance().getTimeInMillis() - last.getCreateTime().getTimeInMillis();
            if (subtract < 30_000) {
                return Result.Failed("spam");
            }
        }

        videoCommentRepository.createComment(text,video,userInfo);
        return Result.Complete();
    }

    @Override
    public boolean isAccessForRead(User currentUser, VideoComment entity) {
        if (entity == null) return false;
        return videoService.isAccessForRead(currentUser,entity.getVideo());
    }
}
