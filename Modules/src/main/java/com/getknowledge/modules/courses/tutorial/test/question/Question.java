package com.getknowledge.modules.courses.tutorial.test.question;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.courses.tutorial.test.Test;
import com.getknowledge.modules.courses.tutorial.test.question.answer.Answer;
import com.getknowledge.platform.annotations.*;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.base.entities.CloneableEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Entity
@Table(name = "question")
public class Question extends AbstractEntity implements CloneableEntity<Question> {

    public String question;

    @ManyToOne
    @JsonIgnore
    private Test test;

    @OneToMany(mappedBy = "question")
    private List<Answer> answerList= new ArrayList<>();

    @OneToOne
    @com.getknowledge.platform.annotations.Access(myself = true)
    private Question originalQuestion;

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

    public Question getOriginalQuestion() {
        return originalQuestion;
    }

    public void setOriginalQuestion(Question originalQuestion) {
        this.originalQuestion = originalQuestion;
    }

    public Boolean getDeleting() {
        return deleting;
    }

    public void setDeleting(Boolean deleting) {
        this.deleting = deleting;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public List<Answer> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<Answer> answerList) {
        this.answerList = answerList;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    @Override
    public Question clone() {
        Question question = new Question();
        question.setOriginalQuestion(getOriginalQuestion());
        question.setAnswerList(getAnswerList());
        question.setDeleting(getDeleting());
        question.setQuestion(getQuestion());
        question.setTest(getTest());
        question.setObjectVersion(this.getObjectVersion());
        return question;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
