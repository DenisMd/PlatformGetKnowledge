package com.getknowledge.modules.courses.tutorial.comments.question;

import com.getknowledge.modules.messages.CommentStatus;
import com.getknowledge.modules.messages.attachments.AttachmentImage;
import com.getknowledge.modules.messages.attachments.AttachmentImageRepository;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoRepository;
import com.getknowledge.platform.annotations.Action;
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

@Service("TutorialQuestionsService")
public class TutorialQuestionsService extends AbstractService {

    @Autowired
    private TutorialQuestionRepository tutorialQuestionRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private AttachmentImageRepository attachmentImageRepository;

    @Autowired
    private TraceService traceService;

    @ActionWithFile(name = "addImageToQuestion" , mandatoryFields = {"questionId"})
    @Transactional
    public Result addImageToPost(HashMap<String,Object> data, List<MultipartFile> fileList){
        Long messageId = longFromField("questionId" , data);
        TutorialQuestion tutorialQuestion = tutorialQuestionRepository.read(messageId);
        if (tutorialQuestion == null) {
            return Result.NotFound();
        }

        UserInfo currentUser = userInfoRepository.getCurrentUser(data);
        if (currentUser == null) {
            return Result.NotAuthorized();
        }

        if (!tutorialQuestion.getSender().equals(currentUser)) {
            return Result.AccessDenied();
        }

        try {
            AttachmentImage attachmentImage = attachmentImageRepository.createAttachmentImage(fileList.get(0).getBytes());
            tutorialQuestion.getImages().add(attachmentImage);
            tutorialQuestionRepository.merge(tutorialQuestion);
        } catch (IOException e) {
            traceService.logException("Error upload image for post : " + e.getMessage(),e,TraceLevel.Error);
            return Result.Failed();
        }
        return Result.Complete();
    }


    @Action(name = "blockQuestion", mandatoryFields = {"questionId","status"})
    @Transactional
    public Result blockComment(HashMap<String,Object> data){

        Long questionId = longFromField("questionId" , data);
        TutorialQuestion tutorialQuestion = tutorialQuestionRepository.read(questionId);

        if (tutorialQuestion == null)
            return Result.NotFound();

        if (!isAccessToEdit(data,tutorialQuestion)){
            return Result.AccessDenied();
        }

        CommentStatus commentStatus = CommentStatus.valueOf((String) data.get("status"));
        if (commentStatus == CommentStatus.Normal) {
            return Result.Failed();
        }
        tutorialQuestionRepository.blockComment(tutorialQuestion, commentStatus);

        return Result.Complete();
    }
}
