package com.getknowledge.modules.userInfo.post.messages;


import com.getknowledge.modules.messages.attachments.AttachmentImage;
import com.getknowledge.modules.messages.attachments.AttachmentImageRepository;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.exceptions.DeleteException;
import com.getknowledge.platform.exceptions.PlatformException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Calendar;

@Repository("PostMessageRepository")
public class PostMessageRepository extends BaseRepository<PostMessage> {
    @Override
    protected Class<PostMessage> getClassEntity() {
        return PostMessage.class;
    }

    @Autowired
    private AttachmentImageRepository attachmentImageRepository;

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

    public PostMessage createComment(UserInfo sender,PostMessage basePost, String text) {
        PostMessage comment = new PostMessage();
        comment.setCreateTime(Calendar.getInstance());
        comment.setMessage(text);
        comment.setSender(sender);
        comment.setBasePost(basePost);
        comment.setComment(true);
        create(comment);
        basePost.getComments().add(comment);
        merge(basePost);
        return comment;
    }

    @Override
    public void remove(PostMessage postMessage) {
        for (AttachmentImage attachmentImage : postMessage.getImages()) {
            attachmentImageRepository.remove(attachmentImage.getId());
        }
        super.remove(postMessage);
    }
}
