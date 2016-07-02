package com.getknowledge.modules.courses.tutorial.comments.review;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.courses.raiting.Rating;
import com.getknowledge.modules.courses.tutorial.Tutorial;
import com.getknowledge.modules.messages.Comment;
import com.getknowledge.modules.messages.Message;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.permission.names.PermissionNames;

import javax.persistence.*;

@Entity
@Table(name = "tutorial_reviews")
@ModuleInfo(repositoryName = "TutorialReviewRepository" , serviceName = "TutorialReviewService")
public class TutorialReview extends Comment {

    @ManyToOne(optional = false)
    @JsonIgnore
    private Tutorial tutorial;

    @OneToOne(optional = false)
    private Rating rating;

    public Tutorial getTutorial() {
        return tutorial;
    }

    public void setTutorial(Tutorial tutorial) {
        this.tutorial = tutorial;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    @Override
    protected Comment createComment() {
        TutorialReview tutorialReview = new TutorialReview();
        tutorialReview.setRating(getRating());
        return tutorialReview;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = super.getAuthorizationList();
        authorizationList.allowReadEveryOne = false;
        return authorizationList;
    }
}
