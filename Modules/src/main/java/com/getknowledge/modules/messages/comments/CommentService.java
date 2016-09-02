package com.getknowledge.modules.messages.comments;

import com.getknowledge.modules.books.Book;
import com.getknowledge.modules.books.comment.BookComment;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoRepository;
import com.getknowledge.modules.userInfo.blocker.BlockerTypes;
import com.getknowledge.modules.userInfo.blocker.UserActionFilterService;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.services.AuthorizedService;
import com.getknowledge.platform.exceptions.NotAuthorized;
import com.getknowledge.platform.modules.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.HashMap;


public abstract class CommentService<T extends Comment> extends AuthorizedService<T> {

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private UserActionFilterService userActionFilterService;

    protected abstract CommentRepository<T> getRepository();

    protected abstract Result createComment(Long objectId, String text, UserInfo sender);

    @Action(name = "blockComment", mandatoryFields = {"commentId","status"})
    @Transactional
    public Result blockComment(HashMap<String,Object> data){

        Long commentId = longFromField("commentId" , data);
        T comment = getRepository().read(commentId);

        if (comment == null)
            return Result.NotFound();

        if (!isAccessToEdit(data,comment)){
            return Result.AccessDenied();
        }

        CommentStatus commentStatus = CommentStatus.valueOf((String) data.get("status"));
        if (commentStatus == CommentStatus.Normal) {
            return Result.Failed();
        }

        getRepository().blockComment(comment, commentStatus);

        return Result.Complete();
    }

    @Action(name = "removeComment" , mandatoryFields = {"commentId"})
    @Transactional
    public Result removeComment(HashMap<String,Object> data) throws NotAuthorized {
        UserInfo userInfo = userInfoRepository.getCurrentUser(data);
        if (userInfo == null) {
            return Result.NotAuthorized();
        }

        Long commentId = longFromField("commentId",data);
        T comment = getRepository().read(commentId);
        if (comment == null) {
            return Result.NotFound();
        }

        if (!comment.getSender().getId().equals(userInfo.getId())) {
            return Result.AccessDenied();
        }

        getRepository().remove(comment);

        return Result.Complete();
    }

    @Action(name = "addComment" , mandatoryFields = {"objectId","text"})
    @Transactional
    public Result addComment(HashMap<String,Object> data) throws NotAuthorized {

        UserInfo userInfo = userInfoRepository.getCurrentUser(data);

        if (userInfo == null) {
            return Result.NotAuthorized();
        }

        Long objectId = longFromField("objectId",data);

        String text = (String) data.get("text");
        if (text.length() > 250) {
            return Result.Failed("max_length");
        }

        if (!userActionFilterService.filterAction(userInfo, (String) data.get("ipAddress"), BlockerTypes.GeneralComments)) {
            return Result.AccessDenied();
        }

        T last = getRepository().getLastComment();

        //Если один и тот же пользователь в течении 30 сек пытается отправить еще одно сообщение отклоняем его как спам
        if (last!= null && last.getSender().getId().equals(userInfo.getId())) {

            long subtract =  Calendar.getInstance().getTimeInMillis() - last.getCreateTime().getTimeInMillis();
            if (subtract < 30_000) {
                return Result.Failed("spam");
            }
        }

        return createComment(objectId, text, userInfo);
    }


}
