package com.getknowledge.modules.messages.post;

import com.getknowledge.modules.messages.attachments.AttachmentImage;
import com.getknowledge.modules.messages.attachments.AttachmentImageRepository;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;

public abstract class PostRepository<T extends Post, T2 extends AbstractEntity> extends ProtectedRepository<T> {

    @Autowired
    private AttachmentImageRepository attachmentImageRepository;

    protected abstract T createPrototype();

    protected abstract void setEntity(T post, T2 entity);

    public T createPost(UserInfo sender,T2 entity, String text) {
        T postMessage = createPrototype();

        postMessage.setCreateTime(Calendar.getInstance());
        postMessage.setMessage(text);
        postMessage.setSender(sender);
        setEntity(postMessage,entity);
        postMessage.setComment(false);
        create(postMessage);
        return postMessage;
    }

    public T createPostComment(UserInfo sender,T basePost, String text) {

        T comment = createPrototype();

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
    public void remove(T post) {
        for (Object attachmentImage : post.getImages()) {
            attachmentImageRepository.remove(((AbstractEntity)attachmentImage).getId());
        }
        super.remove(post);
    }

}
