package com.getknowledge.modules.courses.tutorial.comments.question;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.courses.tutorial.Tutorial;
import com.getknowledge.modules.messages.comments.Comment;
import com.getknowledge.modules.messages.attachments.AttachmentImage;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tutorial_questions")
@ModuleInfo(serviceName = "TutorialQuestionsService")
public class TutorialQuestion extends Comment {

    @OneToMany(cascade = {CascadeType.REMOVE})
    @JsonIgnore
    private List<AttachmentImage> images = new ArrayList<>();

    @Column(nullable = false)
    private Boolean comment = false;

    @OneToMany(mappedBy = "base" , cascade = {CascadeType.REMOVE})
    @JsonIgnore
    private List<TutorialQuestion> comments = new ArrayList<>();

    @ManyToOne
    @JsonIgnore
    private TutorialQuestion base;

    @ManyToOne(optional = false)
    @JsonIgnore
    public Tutorial tutorial;

    public List<TutorialQuestion> getComments() {
        return comments;
    }

    public void setComments(List<TutorialQuestion> comments) {
        this.comments = comments;
    }

    public TutorialQuestion getBase() {
        return base;
    }

    public void setBase(TutorialQuestion base) {
        this.base = base;
    }

    public boolean isComment() {
        return comment;
    }

    public void setComment(boolean comment) {
        this.comment = comment;
    }

    public List<AttachmentImage> getImages() {
        return images;
    }

    public void setImages(List<AttachmentImage> images) {
        this.images = images;
    }

    public Tutorial getTutorial() {
        return tutorial;
    }

    public void setTutorial(Tutorial tutorial) {
        this.tutorial = tutorial;
    }


    @Override
    protected Comment createComment() {
        TutorialQuestion tutorialQuestion = new TutorialQuestion();
        tutorialQuestion.setComment(this.isComment());
        return tutorialQuestion;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = super.getAuthorizationList();
        authorizationList.allowReadEveryOne = false;
        return authorizationList;
    }
}
