package com.getknowledge.modules.programs.comment;

import com.getknowledge.modules.books.Book;
import com.getknowledge.modules.messages.comments.CommentRepository;
import com.getknowledge.modules.messages.comments.CommentService;
import com.getknowledge.modules.messages.comments.CommentStatus;
import com.getknowledge.modules.programs.Program;
import com.getknowledge.modules.programs.ProgramRepository;
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

@Service("ProgramCommentService")
public class ProgramCommentService extends CommentService<ProgramComment> {
    @Autowired
    private ProgramCommentRepository programCommentRepository;

    @Autowired
    private ProgramRepository programRepository;

    @Override
    protected CommentRepository<ProgramComment> getRepository() {
        return programCommentRepository;
    }

    @Override
    protected Result createComment(Long objectId, String text, UserInfo sender) {
        Program program = programRepository.read(objectId);
        if (program == null) {
            return Result.NotFound();
        }

        programCommentRepository.createComment(text, program, sender);
        return Result.Complete();
    }
}
