package com.getknowledge.modules.video.comment;

import com.getknowledge.modules.messages.MessageStatus;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoService;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.modules.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service("VideoCommentService")
public class VideoCommentService extends AbstractService {

    @Autowired
    private VideoCommentRepository videoCommentRepository;

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

        MessageStatus messageStatus = MessageStatus.valueOf((String) data.get("status"));
        if (messageStatus == MessageStatus.Normal) {
            return Result.Failed();
        }
        videoCommentRepository.blockComment(videoComment,messageStatus);

        return Result.Complete();
    }

}
