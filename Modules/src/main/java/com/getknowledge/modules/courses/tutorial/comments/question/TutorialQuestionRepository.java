package com.getknowledge.modules.courses.tutorial.comments.question;


import com.getknowledge.modules.courses.tutorial.Tutorial;
import com.getknowledge.modules.messages.comments.CommentRepository;
import com.getknowledge.modules.messages.attachments.AttachmentImage;
import com.getknowledge.modules.messages.attachments.AttachmentImageRepository;
import com.getknowledge.modules.userInfo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Calendar;

@Repository("TutorialQuestionRepository")
public class TutorialQuestionRepository extends CommentRepository<TutorialQuestion> {

    @Override
    protected String getEntityName() {
        return TutorialQuestion.class.getSimpleName();
    }

    @Override
    protected Class<TutorialQuestion> getClassEntity() {
        return TutorialQuestion.class;
    }

    @Autowired
    private AttachmentImageRepository attachmentImageRepository;

    public TutorialQuestion createMessage(UserInfo sender,Tutorial tutorial, String text) {
        TutorialQuestion tutorialQuestion = new TutorialQuestion();
        tutorialQuestion.setMessage(text);
        tutorialQuestion.setSender(sender);
        tutorialQuestion.setTutorial(tutorial);
        tutorialQuestion.setComment(false);
        create(tutorialQuestion);
        return tutorialQuestion;
    }

    public TutorialQuestion createComment(UserInfo sender,TutorialQuestion basePost, String text) {
        TutorialQuestion comment = new TutorialQuestion();
        comment.setCreateTime(Calendar.getInstance());
        comment.setMessage(text);
        comment.setSender(sender);
        comment.setBase(basePost);
        comment.setComment(true);
        create(comment);
        basePost.getComments().add(comment);
        merge(basePost);
        return comment;
    }

    @Override
    public void remove(TutorialQuestion tutorialQuestion) {
        for (AttachmentImage attachmentImage : tutorialQuestion.getImages()) {
            attachmentImageRepository.remove(attachmentImage.getId());
        }
        super.remove(tutorialQuestion);
    }

}
