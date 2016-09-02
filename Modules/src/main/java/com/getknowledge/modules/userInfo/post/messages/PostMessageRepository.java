package com.getknowledge.modules.userInfo.post.messages;


import com.getknowledge.modules.messages.post.PostRepository;
import com.getknowledge.modules.userInfo.UserInfo;
import org.springframework.stereotype.Repository;

@Repository("PostMessageRepository")
public class PostMessageRepository extends PostRepository<PostMessage,UserInfo> {

    @Override
    protected Class<PostMessage> getClassEntity() {
        return PostMessage.class;
    }

    @Override
    protected PostMessage createPrototype() {
        return new PostMessage();
    }

    @Override
    protected void setEntity(PostMessage post, UserInfo entity) {
        post.setRecipient(entity);
    }
}
