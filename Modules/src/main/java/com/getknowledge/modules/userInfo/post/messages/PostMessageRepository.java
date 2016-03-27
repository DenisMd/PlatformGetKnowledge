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
        create(postMessage);
        return postMessage;
    }

    @Override
    public void remove(Long id) throws PlatformException {
        PostMessage postMessage = read(id);
        if (postMessage != null) {
            for (AttachmentImage attachmentImage : postMessage.getImages()) {
                attachmentImageRepository.remove(attachmentImage.getId());
            }
            super.remove(id);
        } else {
            throw new DeleteException("Post message with id " + id + "not found");
        }
    }
}
