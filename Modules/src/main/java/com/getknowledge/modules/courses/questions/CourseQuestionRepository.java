package com.getknowledge.modules.courses.questions;

import com.getknowledge.modules.courses.Course;
import com.getknowledge.modules.messages.attachments.AttachmentImage;
import com.getknowledge.modules.messages.attachments.AttachmentImageRepository;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Calendar;


@Repository("CourseQuestionRepository")
public class CourseQuestionRepository extends ProtectedRepository<CourseQuestion> {

    @Override
    protected Class<CourseQuestion> getClassEntity() {
        return CourseQuestion.class;
    }

    @Autowired
    private AttachmentImageRepository attachmentImageRepository;

    public CourseQuestion createMessage(UserInfo sender, Course course, String text) {
        CourseQuestion courseQuestion = new CourseQuestion();
        courseQuestion.setCreateTime(Calendar.getInstance());
        courseQuestion.setMessage(text);
        courseQuestion.setSender(sender);
        courseQuestion.setCourse(course);
        courseQuestion.setComment(false);
        create(courseQuestion);
        return courseQuestion;
    }

    public CourseQuestion createComment(UserInfo sender, CourseQuestion basePost, String text) {
        CourseQuestion comment = new CourseQuestion();
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
    public void remove(CourseQuestion courseQuestion) {
        for (AttachmentImage attachmentImage : courseQuestion.getImages()) {
            attachmentImageRepository.remove(attachmentImage.getId());
        }
        super.remove(courseQuestion);
    }
}
