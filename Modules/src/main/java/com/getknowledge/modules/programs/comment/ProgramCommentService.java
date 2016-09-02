package com.getknowledge.modules.programs.comment;

import com.getknowledge.modules.messages.comments.CommentStatus;
import com.getknowledge.modules.programs.Program;
import com.getknowledge.modules.programs.ProgramRepository;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoRepository;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.exceptions.NotAuthorized;
import com.getknowledge.platform.modules.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.HashMap;

@Service("ProgramCommentService")
public class ProgramCommentService extends AbstractService {
    @Autowired
    private ProgramCommentRepository programCommentRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private ProgramRepository programRepository;

    @Action(name = "blockComment", mandatoryFields = {"commentId","status"})
    @Transactional
    public Result blockComment(HashMap<String,Object> data){

        Long commentId = longFromField("commentId" , data);
        ProgramComment programComment = programCommentRepository.read(commentId);

        if (programComment == null)
            return Result.NotFound();

        if (!isAccessToEdit(data,programComment)){
            return Result.AccessDenied();
        }

        CommentStatus commentStatus = CommentStatus.valueOf((String) data.get("status"));
        if (commentStatus == CommentStatus.Normal) {
            return Result.Failed();
        }
        programCommentRepository.blockComment(programComment, commentStatus);

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
        ProgramComment bookComment = programCommentRepository.read(videoCommentId);
        if (bookComment == null) {
            return Result.NotFound();
        }

        if (!bookComment.getSender().getId().equals(userInfo.getId())) {
            return Result.AccessDenied();
        }

        programCommentRepository.remove(bookComment);

        return Result.Complete();
    }

    @Action(name = "addComment" , mandatoryFields = {"objectId","text"})
    @Transactional
    public Result addComment(HashMap<String,Object> data) throws NotAuthorized {
        UserInfo userInfo = userInfoRepository.getCurrentUser(data);
        if (userInfo == null) {
            return Result.NotAuthorized();
        }

        Long bookId = longFromField("objectId",data);
        Program program = programRepository.read(bookId);

        if (program == null){
            return Result.NotFound();
        }

        String text = (String) data.get("text");
        if (text.length() > 250) {
            return Result.Failed("max_length");
        }

        ProgramComment last = programCommentRepository.getLastComment();

        //Если один и тот же пользователь в течении 30 сек пытается отправить еще одно сообщение отклоняем его как спам
        if (last!= null && last.getSender().getId().equals(userInfo.getId())) {

            long subtract =  Calendar.getInstance().getTimeInMillis() - last.getCreateTime().getTimeInMillis();
            if (subtract < 30_000) {
                return Result.Failed("spam");
            }
        }

        programCommentRepository.createComment(text,program,userInfo);
        return Result.Complete();
    }
}
