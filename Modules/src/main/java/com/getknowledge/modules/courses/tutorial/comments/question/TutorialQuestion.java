package com.getknowledge.modules.courses.tutorial.comments.question;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.courses.tutorial.Tutorial;
import com.getknowledge.modules.messages.Comment;
import com.getknowledge.modules.messages.Message;
import com.getknowledge.modules.messages.attachments.AttachmentImage;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.permission.names.PermissionNames;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tutorial_questions")
@ModuleInfo(serviceName = "TutorialQuestionsService")
public class TutorialQuestion extends Comment {

    @OneToMany
    @JsonIgnore
    private List<AttachmentImage> images = new ArrayList<>();

    private Boolean comment = false;

    @OneToMany(mappedBy = "base")
    @JsonIgnore
    private List<TutorialQuestion> comments = new ArrayList<>();

    @ManyToOne
    @JsonIgnore
    private TutorialQuestion base;

    @ManyToOne
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
    public AbstractEntity clone() {
        TutorialQuestion tutorialQuestion = new TutorialQuestion();
        tutorialQuestion.setComment(this.isComment());
        tutorialQuestion.setCreateTime(this.getCreateTime());
        tutorialQuestion.setMessage(this.getMessage());
        tutorialQuestion.setSender(this.getSender());
        tutorialQuestion.setId(this.getId());
        tutorialQuestion.setObjectVersion(this.getObjectVersion());
        return tutorialQuestion;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.getPermissionsForEdit().add(new Permission(PermissionNames.BlockComments));
        return authorizationList;
    }
}
