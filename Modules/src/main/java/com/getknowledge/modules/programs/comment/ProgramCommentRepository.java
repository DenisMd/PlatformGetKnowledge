package com.getknowledge.modules.programs.comment;

import com.getknowledge.modules.messages.comments.CommentRepository;
import com.getknowledge.modules.programs.Program;
import com.getknowledge.modules.userInfo.UserInfo;
import org.springframework.stereotype.Repository;

@Repository("ProgramCommentRepository")
public class ProgramCommentRepository extends CommentRepository<ProgramComment>{
    @Override
    protected Class<ProgramComment> getClassEntity() {
        return ProgramComment.class;
    }

    @Override
    protected String getEntityName() {
        return ProgramComment.class.getSimpleName();
    }

    public void createComment(String text, Program program, UserInfo userInfo) {
        ProgramComment bookComment = new ProgramComment();
        bookComment.setProgram(program);
        bookComment.setMessage(text);
        bookComment.setSender(userInfo);
        create(bookComment);
    }
}
