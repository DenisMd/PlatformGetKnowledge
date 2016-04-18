package com.getknowledge.modules.userInfo.courseInfo.tutorialInfo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.courses.tutorial.Tutorial;
import com.getknowledge.modules.userInfo.courseInfo.CourseInfo;
import com.getknowledge.modules.userInfo.courseInfo.tutorialInfo.questionInfo.QuestionInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tutorial_info")
public class TutorialInfo extends AbstractEntity {

    @ManyToOne(optional = false)
    @JsonIgnore
    private CourseInfo courseInfo;

    @ManyToOne(optional = false)
    @JsonIgnore
    private Tutorial tutorial;

    @OneToMany(mappedBy = "tutorialInfo")
    private List<QuestionInfo> questionInfoList = new ArrayList<>();

    public List<QuestionInfo> getQuestionInfoList() {
        return questionInfoList;
    }

    public void setQuestionInfoList(List<QuestionInfo> questionInfoList) {
        this.questionInfoList = questionInfoList;
    }

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
