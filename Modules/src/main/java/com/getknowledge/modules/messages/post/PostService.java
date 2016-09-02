package com.getknowledge.modules.messages.post;

import com.getknowledge.modules.messages.attachments.AttachmentImage;
import com.getknowledge.modules.messages.attachments.AttachmentImageRepository;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoRepository;
import com.getknowledge.modules.userInfo.post.messages.PostMessage;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.annotations.ActionWithFile;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.modules.Result;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public abstract class PostService<T extends Post, T2 extends AbstractEntity> extends AbstractService {

    protected abstract PostRepository<T,T2> getRepository();

    protected abstract T2 getEntity(long objectId);

    protected abstract String getEntityName();

    @Autowired
    protected UserInfoRepository userInfoRepository;

    @Autowired
    protected AttachmentImageRepository attachmentImageRepository;

    @Autowired
    protected TraceService traceService;

    @ActionWithFile(name = "addImageToPost", mandatoryFields = {"messageId"})
    @Transactional
    public Result addImageToPost(HashMap<String, Object> data, List<MultipartFile> fileList) {

        Long messageId = longFromField("messageId", data);
        T postMessage = getRepository().read(messageId);
        if (postMessage == null) {
            return Result.NotFound();
        }

        UserInfo currentUser = userInfoRepository.getCurrentUser(data);
        if (currentUser == null) {
            return Result.NotAuthorized();
        }

        if (!postMessage.getSender().equals(currentUser)) {
            return Result.AccessDenied();
        }

        try {
            AttachmentImage attachmentImage = attachmentImageRepository.createAttachmentImage(fileList.get(0).getBytes());
            postMessage.getImages().add(attachmentImage);
            getRepository().merge(postMessage);
        } catch (IOException e) {
            traceService.logException("Error upload image for post : " + e.getMessage(), e, TraceLevel.Error, true);
            return Result.Failed();
        }
        return Result.Complete();
    }

    @Action(name = "getPosts" , mandatoryFields = {"objectId","first","max"})
    @Transactional
    public List<T> getPosts(HashMap<String,Object> data){

        T2 entity = getEntity(longFromField("objectId", data));;
        if (entity == null)
            return null;

        int first = (int) data.get("first");
        int max = (int) data.get("max");


        List<T> messages = entityManager.createQuery("select mess from " + getRepository().getClass().getSimpleName() + " mess " +
                "where mess." + getEntityName() + ".id = :objectId order by mess.createTime desc")
                .setParameter("objectId", entity.getId())
                .setFirstResult(first)
                .setMaxResults(max)
                .getResultList();
        return messages;
    }

    @Action(name = "addPost" , mandatoryFields = {"objectId","text"})
    @Transactional
    public Result addPost(HashMap<String,Object> data){

        T2 entity = getEntity(longFromField("objectId",data));

        if (entity == null)
            return Result.NotFound();

        UserInfo currentUser = userInfoRepository.getCurrentUser(data);
        if (currentUser == null)
            return  Result.NotAuthorized();


        String textMessage = (String) data.get("text");
        getRepository().createPost(currentUser, entity, textMessage);

        return Result.Complete();
    }

    @Action(name = "addCommentToPost" , mandatoryFields = {"postId","text"})
    @Transactional
    public Result addCommentToPost(HashMap<String,Object> data){
        UserInfo currentUser = userInfoRepository.getCurrentUser(data);
        if (currentUser == null)
            return  Result.NotAuthorized();

        PostMessage basePost = postMessageRepository.read(longFromField("postId",data));
        if (basePost == null) {
            return Result.NotFound();
        }
        String textMessage = (String) data.get("text");
        postMessageRepository.createComment(currentUser,basePost,textMessage);

        return Result.Complete();
    }

    @Action(name = "removePost" , mandatoryFields = {"postId"})
    @Transactional
    public Result removePost(HashMap<String,Object> data) throws PlatformException {
        UserInfo userInfo = userInfoRepository.getCurrentUser(data);
        if (userInfo == null)
            return Result.NotAuthorized();

        Long postId = longFromField("postId",data);
        PostMessage postMessage = postMessageRepository.read(postId);
        if (postMessage == null) {
            return Result.NotFound();
        }

        if (postMessage.getSender().equals(userInfo) || postMessage.getRecipient().equals(userInfo)) {
            postMessageRepository.remove(postId);
        } else {
            return Result.NotAuthorized();
        }

        return Result.Complete();
    }
}