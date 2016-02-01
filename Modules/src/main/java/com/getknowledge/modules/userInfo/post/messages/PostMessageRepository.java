package com.getknowledge.modules.userInfo.post.messages;


import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("PostMessageRepository")
public class PostMessageRepository extends BaseRepository<PostMessage> {
    @Override
    protected Class<PostMessage> getClassEntity() {
        return PostMessage.class;
    }
}
