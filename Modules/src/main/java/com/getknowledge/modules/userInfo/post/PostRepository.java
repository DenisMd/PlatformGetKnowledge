package com.getknowledge.modules.userInfo.post;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("PostRepository")
public class PostRepository extends BaseRepository<Post> {
    @Override
    protected Class<Post> getClassEntity() {
        return Post.class;
    }
}
