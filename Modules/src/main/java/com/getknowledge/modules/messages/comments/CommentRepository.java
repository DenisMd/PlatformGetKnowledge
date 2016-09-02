package com.getknowledge.modules.messages.comments;


import com.getknowledge.platform.base.repositories.ProtectedRepository;

import java.util.Calendar;
import java.util.List;

public abstract class CommentRepository<T extends Comment> extends ProtectedRepository<T> {

    protected abstract String getEntityName();

    public void blockComment(T comment, CommentStatus commentStatus) {
        comment.setMessage("");
        comment.setCommentStatus(commentStatus);
        merge(comment);
    }

    @Override
    public void create(T object) {
        object.setCreateTime(Calendar.getInstance());
        object.setCommentStatus(CommentStatus.Normal);
        super.create(object);
    }

    public T getLastComment() {
        List<T> bookComments = entityManager.createQuery(
                "select c from "+getEntityName()+" c " +
                        "where c.createTime = (select max(c2.createTime) from "+getEntityName()+" c2)"
        ).getResultList();

        return bookComments.isEmpty() ? null : bookComments.get(0);
    }
}
