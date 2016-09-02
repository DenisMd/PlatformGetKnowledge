package com.getknowledge.modules.courses.questions;

import com.getknowledge.modules.courses.Course;
import com.getknowledge.modules.messages.post.PostRepository;
import org.springframework.stereotype.Repository;


@Repository("CourseQuestionRepository")
public class CourseQuestionRepository extends PostRepository<CourseQuestion, Course> {

    @Override
    protected Class<CourseQuestion> getClassEntity() {
        return CourseQuestion.class;
    }

    @Override
    protected CourseQuestion createPrototype() {
        return new CourseQuestion();
    }

    @Override
    protected void setEntity(CourseQuestion post, Course entity) {
        post.setCourse(entity);
    }
}
