package com.getknowledge.modules.userInfo.post.messages;

import com.getknowledge.modules.messages.attachments.AttachmentImage;
import com.getknowledge.modules.messages.attachments.AttachmentImageRepository;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoRepository;
import com.getknowledge.platform.annotations.ActionWithFile;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.modules.Result;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Service("PostMessageService")
public class PostMessageService extends AbstractService {

    @Autowired
    private PostMessageRepository postMessageRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private AttachmentImageRepository attachmentImageRepository;

    @Autowired
    private TraceService traceService;

    @ActionWithFile(name = "addImageToPost" , mandatoryFields = {"messageId"})
    @Transactional
    public Result addImageToPost(HashMap<String,Object> data, List<MultipartFile> fileList){
        Long messageId = longFromField("messageId" , data);
        PostMessage postMessage = postMessageRepository.read(messageId);
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
            postMessageRepository.merge(postMessage);
        } catch (IOException e) {
            traceService.logException("Error upload image for post : " + e.getMessage(),e,TraceLevel.Error,true);
            return Result.Failed();
        }
        return Result.Complete();
    }
}
