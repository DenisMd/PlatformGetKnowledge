package com.getknowledge.modules.books.comment;

import com.getknowledge.modules.books.Book;
import com.getknowledge.modules.books.BookRepository;
import com.getknowledge.modules.messages.CommentStatus;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoRepository;
import com.getknowledge.modules.video.Video;
import com.getknowledge.modules.video.comment.VideoComment;
import com.getknowledge.modules.video.comment.VideoCommentRepository;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.exceptions.NotAuthorized;
import com.getknowledge.platform.modules.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.HashMap;

@Service("BookCommentService")
public class BookCommentService extends AbstractService {
    @Autowired
    private BookCommentRepository bookCommentRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private BookRepository bookRepository;

    @Action(name = "blockComment", mandatoryFields = {"commentId","status"})
    @Transactional
    public Result blockComment(HashMap<String,Object> data){

        Long commentId = longFromField("commentId" , data);
        BookComment bookComment = bookCommentRepository.read(commentId);

        if (bookComment == null)
            return Result.NotFound();

        if (!isAccessToEdit(data,bookComment)){
            return Result.AccessDenied();
        }

        CommentStatus commentStatus = CommentStatus.valueOf((String) data.get("status"));
        if (commentStatus == CommentStatus.Normal) {
            return Result.Failed();
        }
        bookCommentRepository.blockComment(bookComment, commentStatus);

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
        BookComment bookComment = bookCommentRepository.read(videoCommentId);
        if (bookComment == null) {
            return Result.NotFound();
        }

        if (!bookComment.getSender().getId().equals(userInfo.getId())) {
            return Result.AccessDenied();
        }

        bookCommentRepository.remove(bookComment);

        return Result.Complete();
    }

    @Action(name = "addComment" , mandatoryFields = {"bookId","text"})
    @Transactional
    public Result addComment(HashMap<String,Object> data) throws NotAuthorized {
        UserInfo userInfo = userInfoRepository.getCurrentUser(data);
        if (userInfo == null) {
            return Result.NotAuthorized();
        }

        Long bookId = longFromField("bookId",data);
        Book book = bookRepository.read(bookId);

        if (book == null){
            return Result.NotFound();
        }

        String text = (String) data.get("text");
        if (text.length() > 250) {
            return Result.Failed("max_length");
        }

        BookComment last = bookCommentRepository.getLastComment();

        //Если один и тот же пользователь в течении 30 сек пытается отправить еще одно сообщение отклоняем его как спам
        if (last!= null && last.getSender().getId().equals(userInfo.getId())) {

            long subtract =  Calendar.getInstance().getTimeInMillis() - last.getCreateTime().getTimeInMillis();
            if (subtract < 30_000) {
                return Result.Failed("spam");
            }
        }

        bookCommentRepository.createComment(text,book,userInfo);
        return Result.Complete();
    }
}
