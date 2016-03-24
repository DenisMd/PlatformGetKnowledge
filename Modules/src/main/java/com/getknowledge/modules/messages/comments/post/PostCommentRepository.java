package com.getknowledge.modules.messages.comments.post;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("PostCommentRepository")
public class PostCommentRepository extends BaseRepository<PostComment> {
    @Override
    protected Class<PostComment> getClassEntity() {
        return PostComment.class;
    }
}
