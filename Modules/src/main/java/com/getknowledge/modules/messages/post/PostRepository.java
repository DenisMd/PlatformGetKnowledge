package com.getknowledge.modules.messages.post;

import com.getknowledge.modules.messages.attachments.AttachmentImage;
import com.getknowledge.modules.messages.attachments.AttachmentImageRepository;
import com.getknowledge.modules.messages.post.Post;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.post.messages.PostMessage;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;

public abstract class PostRepository<T extends Post> extends ProtectedRepository<T> {

    @Autowired
    private AttachmentImageRepository attachmentImageRepository;


    public PostMessage createComment(UserInfo sender,Post basePost, String text) {
        Post comment = basePost.createNewPost();
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
