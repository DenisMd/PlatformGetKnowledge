package com.getknowledge.modules.books.comment;

import com.getknowledge.modules.books.Book;
import com.getknowledge.modules.interfaces.repos.ICommentRepository;
import com.getknowledge.modules.messages.CommentStatus;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.video.Video;
import com.getknowledge.modules.video.comment.VideoComment;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.List;

@Repository("BookCommentRepository")
public class BookCommentRepository extends ProtectedRepository<BookComment> implements ICommentRepository<BookComment> {
    @Override
    protected Class<BookComment> getClassEntity() {
        return BookComment.class;
    }

    public void createComment(String text, Book book, UserInfo userInfo) {
        BookComment bookComment = new BookComment();
        bookComment.setBook(book);
        bookComment.setCreateTime(Calendar.getInstance());
        bookComment.setMessage(text);
        bookComment.setCommentStatus(CommentStatus.Normal);
        bookComment.setSender(userInfo);
        create(bookComment);
    }

    @Override
    public void blockComment(BookComment bookComment, CommentStatus commentStatus) {
        bookComment.setMessage("");
        bookComment.setCommentStatus(commentStatus);
        merge(bookComment);
    }

    @Override
    public BookComment getLastComment() {
        List<BookComment> bookComments = entityManager.createQuery(
                "select bc from BookComment bc " +
                        "where bc.createTime = (select max(bc2.createTime) from BookComment bc2)"
        ).getResultList();

        return bookComments.isEmpty() ? null : bookComments.get(0);
    }
}
