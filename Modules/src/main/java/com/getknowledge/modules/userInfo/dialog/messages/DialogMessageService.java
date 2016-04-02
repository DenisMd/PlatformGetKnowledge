package com.getknowledge.modules.userInfo.dialog.messages;

import com.getknowledge.modules.messages.attachments.AttachmentImage;
import com.getknowledge.modules.messages.attachments.AttachmentImageRepository;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoRepository;
import com.getknowledge.modules.userInfo.post.messages.PostMessage;
import com.getknowledge.modules.userInfo.post.messages.PostMessageRepository;
import com.getknowledge.platform.annotations.ActionWithFile;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.modules.Result;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Service("DialogMessageService")
public class DialogMessageService extends AbstractService {
    @Autowired
    private DialogMessageRepository dialogMessageRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private AttachmentImageRepository attachmentImageRepository;

    @Autowired
    private TraceService traceService;

    @ActionWithFile(name = "addImageToDialogMessage" , mandatoryFields = {"messageId"})
    @Transactional
    public Result addImageToDialogMessage(HashMap<String,Object> data, List<MultipartFile> fileList){
        Long messageId = longFromField("messageId" , data);
        DialogMessage dialogMessage = dialogMessageRepository.read(messageId);
        if (dialogMessage == null) {
            return Result.NotFound();
        }

        UserInfo currentUser = userInfoRepository.getCurrentUser(data);
        if (currentUser == null) {
            return Result.NotAuthorized();
        }

        if (!dialogMessage.getSender().equals(currentUser)) {
            return Result.AccessDenied();
        }

        try {
            AttachmentImage attachmentImage = attachmentImageRepository.createAttachmentImage(fileList.get(0).getBytes());
            dialogMessage.getImages().add(attachmentImage);
            dialogMessageRepository.merge(dialogMessage);
        } catch (IOException e) {
            traceService.logException("Error upload image for post : " + e.getMessage(),e, TraceLevel.Error);
            return Result.Failed();
        }
        return Result.Complete();
    }
}
