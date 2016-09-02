package com.getknowledge.modules.books.comment;

import com.getknowledge.modules.books.Book;
import com.getknowledge.modules.books.BookRepository;
import com.getknowledge.modules.messages.comments.CommentRepository;
import com.getknowledge.modules.messages.comments.CommentService;
import com.getknowledge.modules.messages.comments.CommentStatus;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoRepository;
import com.getknowledge.modules.video.comment.VideoComment;
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
public class BookCommentService extends CommentService<BookComment> {

    @Autowired
    private BookCommentRepository bookCommentRepository;

    @Autowired
    private BookRepository bookRepository;

    @Override
    protected CommentRepository<BookComment> getRepository() {
        return bookCommentRepository;
    }

    @Override
    protected Result createComment(Long objectId, String text, UserInfo sender) {
        Book book = bookRepository.read(objectId);
        if (book == null) {
            return Result.NotFound();
        }

        bookCommentRepository.createComment(text, book, sender);
        return Result.Complete();
    }
}
