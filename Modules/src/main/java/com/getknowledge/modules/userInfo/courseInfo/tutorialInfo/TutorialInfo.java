package com.getknowledge.modules.userInfo.courseInfo.tutorialInfo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.courses.tutorial.Tutorial;
import com.getknowledge.modules.userInfo.courseInfo.CourseInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tutorial_info")
public class TutorialInfo extends AbstractEntity {

    @ManyToOne
    @JsonIgnore
    private CourseInfo courseInfo;

    @ManyToOne
    @JsonIgnore
    private Tutorial tutorial;


    public CourseInfo getCourseInfo() {
        return courseInfo;
    }

    public void setCourseInfo(CourseInfo courseInfo) {
        this.courseInfo = courseInfo;
    }

    public Tutorial getTutorial() {
        return tutorial;
    }

    public void setTutorial(Tutorial tutorial) {
        this.tutorial = tutorial;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
