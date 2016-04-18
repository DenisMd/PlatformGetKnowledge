package com.getknowledge.modules.userInfo.courseInfo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.courses.Course;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.courseInfo.tutorialInfo.TutorialInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "course_info")
public class CourseInfo extends AbstractEntity {

    @JsonIgnore
    @ManyToOne(optional = false)
    private UserInfo userInfo;

    @ManyToOne(optional = false)
    @JsonIgnore
    private Course course;

    @OneToMany(mappedBy = "courseInfo")
    private List<TutorialInfo> tutorialInfoList = new ArrayList<>();

    public List<TutorialInfo> getTutorialInfoList() {
        return tutorialInfoList;
    }

    public void setTutorialInfoList(List<TutorialInfo> tutorialInfoList) {
        this.tutorialInfoList = tutorialInfoList;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
