package com.getknowledge.modules.userInfo.post.messages;


import com.getknowledge.modules.messages.attachments.AttachmentImage;
import com.getknowledge.modules.messages.attachments.AttachmentImageRepository;
import com.getknowledge.modules.messages.post.PostRepository;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Calendar;

@Repository("PostMessageRepository")
public class PostMessageRepository extends PostRepository<PostMessage> {

    @Override
    protected Class<PostMessage> getClassEntity() {
        return PostMessage.class;
    }


    public PostMessage createMessage(UserInfo sender,UserInfo recipient, String text) {
        PostMessage postMessage = new PostMessage();
        postMessage.setCreateTime(Calendar.getInstance());
        postMessage.setMessage(text);
        postMessage.setSender(sender);
        postMessage.setRecipient(recipient);
        postMessage.setComment(false);
        create(postMessage);
        return postMessage;
    }




}
