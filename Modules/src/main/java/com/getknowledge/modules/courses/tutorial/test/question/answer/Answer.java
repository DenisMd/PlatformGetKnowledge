package com.getknowledge.modules.courses.tutorial.test.question.answer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.courses.tutorial.test.question.Question;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.base.entities.CloneableEntity;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Table(name = "answer")
public class Answer extends AbstractEntity implements CloneableEntity<Answer> {

    private String answer;

    @JsonIgnore
    private String description;

    @JsonIgnore
    private boolean correct;

    @ManyToOne
    @JsonIgnore
    private Question question;

    @OneToOne
    @com.getknowledge.platform.annotations.Access(myself = true)
    private Answer originalAnswer;

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

    public Answer getOriginalAnswer() {
        return originalAnswer;
    }

    public void setOriginalAnswer(Answer originalAnswer) {
        this.originalAnswer = originalAnswer;
    }

    public Boolean getDeleting() {
        return deleting;
    }

    public void setDeleting(Boolean deleting) {
        this.deleting = deleting;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    @Override
    public Answer clone() {
        Answer answer = new Answer();
        answer.setDeleting(getDeleting());
        answer.setAnswer(getAnswer());
        answer.setCorrect(isCorrect());
        answer.setDescription(getDescription());
        answer.setOriginalAnswer(getOriginalAnswer());
        answer.setQuestion(getQuestion());
        answer.setObjectVersion(this.getObjectVersion());
        return answer;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
