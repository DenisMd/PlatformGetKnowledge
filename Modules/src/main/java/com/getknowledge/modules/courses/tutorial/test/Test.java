package com.getknowledge.modules.courses.tutorial.test;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.courses.tutorial.test.question.Question;
import com.getknowledge.platform.annotations.Access;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.base.entities.CloneableEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Entity
@Table(name = "test")
public class Test extends CloneableEntity<Test> {

    @OneToMany(mappedBy = "test")
    private List<Question> questionList = new ArrayList<>();

    @OneToOne
    @Access(myself = true)
    private Test originalTest;

    @com.getknowledge.platform.annotations.Access(myself = true)
    private Boolean deleting = false;

    @Column(name = "last_change_time")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Calendar lastChangeTime;


    public Calendar getLastChangeTime() {
        return lastChangeTime;
    }

    public void setLastChangeTime(Calendar lastChangeTime) {
        this.lastChangeTime = lastChangeTime;
    }

    public Test getOriginalTest() {
        return originalTest;
    }

    public void setOriginalTest(Test originalTest) {
        this.originalTest = originalTest;
    }

    public Boolean getDeleting() {
        return deleting;
    }

    public void setDeleting(Boolean deleting) {
        this.deleting = deleting;
    }

    public List<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<Question> questionList) {
        this.questionList = questionList;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }

    @Override
    public Test clone() {
        Test test = new Test();
        test.setQuestionList(getQuestionList());
        test.setOriginalTest(getOriginalTest());
        return test;
    }
}
