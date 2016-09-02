package com.getknowledge.modules.courses.questions;

import com.getknowledge.modules.courses.Course;
import com.getknowledge.modules.messages.post.PostRepository;
import com.getknowledge.modules.messages.post.PostService;
import com.getknowledge.platform.base.services.AbstractService;
import com.sun.xml.internal.ws.developer.Serialization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("CourseQuestionService")
public class CourseQuestionService extends PostService<CourseQuestion, Course> {

    @Autowired
    private CourseQuestionRepository courseQuestionRepository;

    @Override
    protected PostRepository<CourseQuestion, Course> getRepository() {
        return courseQuestionRepository;
    }
}
