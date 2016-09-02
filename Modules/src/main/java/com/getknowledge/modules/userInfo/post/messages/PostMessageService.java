package com.getknowledge.modules.userInfo.post.messages;

import com.getknowledge.modules.messages.post.PostRepository;
import com.getknowledge.modules.messages.post.PostService;
import com.getknowledge.modules.userInfo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("PostMessageService")
public class PostMessageService extends PostService<PostMessage, UserInfo> {

    @Autowired
    private PostMessageRepository postMessageRepository;

    @Override
    protected PostRepository<PostMessage, UserInfo> getRepository() {
        return postMessageRepository;
    }

    @Override
    protected UserInfo getEntity(long objectId) {
        return userInfoRepository.read(objectId);
    }

    @Override
    protected String getEntityName() {
        return "recipient";
    }
}
