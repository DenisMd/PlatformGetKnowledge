package com.getknowledge.modules.programs.comment;

import com.getknowledge.modules.books.Book;
import com.getknowledge.modules.interfaces.repos.ICommentRepository;
import com.getknowledge.modules.messages.CommentStatus;
import com.getknowledge.modules.programs.Program;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.List;

@Repository("ProgramCommentRepository")
public class ProgramCommentRepository extends ProtectedRepository<ProgramComment> implements ICommentRepository<ProgramComment> {
    @Override
    protected Class<ProgramComment> getClassEntity() {
        return ProgramComment.class;
    }

    public void createComment(String text, Program program, UserInfo userInfo) {
        ProgramComment bookComment = new ProgramComment();
        bookComment.setProgram(program);
        bookComment.setCreateTime(Calendar.getInstance());
        bookComment.setMessage(text);
        bookComment.setCommentStatus(CommentStatus.Normal);
        bookComment.setSender(userInfo);
        create(bookComment);
    }

    @Override
    public void blockComment(ProgramComment bookComment, CommentStatus commentStatus) {
        bookComment.setMessage("");
        bookComment.setCommentStatus(commentStatus);
        merge(bookComment);
    }

    @Override
    public ProgramComment getLastComment() {
        List<ProgramComment> programComments = entityManager.createQuery(
                "select pc from ProgramComment pc " +
                        "where pc.createTime = (select max(pc2.createTime) from ProgramComment pc2)"
        ).getResultList();

        return programComments.isEmpty() ? null : programComments.get(0);
    }
}
