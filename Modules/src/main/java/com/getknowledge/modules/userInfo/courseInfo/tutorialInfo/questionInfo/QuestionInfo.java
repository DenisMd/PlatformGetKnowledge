package com.getknowledge.modules.userInfo.courseInfo.tutorialInfo.questionInfo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.courses.tutorial.Tutorial;
import com.getknowledge.modules.courses.tutorial.test.question.Question;
import com.getknowledge.modules.userInfo.courseInfo.tutorialInfo.TutorialInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "question_info")
public class QuestionInfo extends AbstractEntity {

    @ManyToOne
    @JsonIgnore
    private TutorialInfo tutorialInfo;

    @ManyToOne
    private Question question;

    private String answers;

    private int score;

    public TutorialInfo getTutorialInfo() {
        return tutorialInfo;
    }

    public void setTutorialInfo(TutorialInfo tutorialInfo) {
        this.tutorialInfo = tutorialInfo;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
