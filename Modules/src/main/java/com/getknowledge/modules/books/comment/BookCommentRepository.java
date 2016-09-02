package com.getknowledge.modules.books.comment;

import com.getknowledge.modules.books.Book;
import com.getknowledge.modules.messages.comments.CommentRepository;
import com.getknowledge.modules.userInfo.UserInfo;
import org.springframework.stereotype.Repository;

@Repository("BookCommentRepository")
public class BookCommentRepository extends CommentRepository<BookComment> {

    @Override
    protected String getEntityName() {
        return BookComment.class.getSimpleName();
    }

    @Override
    protected Class<BookComment> getClassEntity() {
        return BookComment.class;
    }

    public void createComment(String text, Book book, UserInfo userInfo) {
        BookComment bookComment = new BookComment();
        bookComment.setBook(book);
        bookComment.setMessage(text);
        bookComment.setSender(userInfo);
        create(bookComment);
    }
}
