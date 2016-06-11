package com.getknowledge.modules.interfaces.repos;


import com.getknowledge.modules.messages.Comment;
import com.getknowledge.modules.messages.CommentStatus;
import com.getknowledge.modules.video.comment.VideoComment;

public interface ICommentRepository<T extends Comment> {
    void blockComment(T comment, CommentStatus commentStatus);
    T getLastComment();
}
