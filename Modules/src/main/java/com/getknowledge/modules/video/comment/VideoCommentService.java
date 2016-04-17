package com.getknowledge.modules.video.comment;

import com.getknowledge.modules.messages.CommentStatus;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoRepository;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.exceptions.NotAuthorized;
import com.getknowledge.platform.modules.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service("VideoCommentService")
public class VideoCommentService extends AbstractService {

    @Autowired
    private VideoCommentRepository videoCommentRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Action(name = "blockComment", mandatoryFields = {"videoCommentId","status"})
    @Transactional
    public Result blockComment(HashMap<String,Object> data){

        Long commentId = longFromField("videoCommentId" , data);
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

    @Action(name = "removeVideoComment" , mandatoryFields = {"videoCommentId"})
    @Transactional
    public Result removeVideoComment(HashMap<String,Object> data) throws NotAuthorized {
        UserInfo userInfo = userInfoRepository.getCurrentUser(data);
        if (userInfo == null) {
            return Result.NotAuthorized();
        }

        Long videoCommentId = longFromField("videoCommentId",data);
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

}
