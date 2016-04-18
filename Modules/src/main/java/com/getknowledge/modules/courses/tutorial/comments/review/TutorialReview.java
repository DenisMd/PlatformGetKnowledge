package com.getknowledge.modules.courses.tutorial.comments.review;

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
    public AbstractEntity clone() {
        TutorialReview tutorialReview = new TutorialReview();
        tutorialReview.setRating(this.getRating());
        tutorialReview.setCreateTime(this.getCreateTime());
        tutorialReview.setMessage(this.getMessage());
        tutorialReview.setSender(this.getSender());
        tutorialReview.setId(this.getId());
        tutorialReview.setObjectVersion(this.getObjectVersion());
        return tutorialReview;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.getPermissionsForEdit().add(new Permission(PermissionNames.BlockComments));
        return authorizationList;
    }
}
