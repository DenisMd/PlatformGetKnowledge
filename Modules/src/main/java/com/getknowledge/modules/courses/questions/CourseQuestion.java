package com.getknowledge.modules.courses.questions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.courses.Course;
import com.getknowledge.modules.messages.post.Post;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "course_questions")
@ModuleInfo(serviceName = "CourseQuestionService", repositoryName = "CourseQuestionRepository")
public class CourseQuestion extends Post<CourseQuestion> {

    @ManyToOne
    @JsonIgnore
    private Course course;

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Override
    public Post createNewPost() {
        return new CourseQuestion();
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }

}
