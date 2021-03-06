package com.getknowledge.modules.courses.tutorial;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.courses.Course;
import com.getknowledge.modules.courses.raiting.Rating;
import com.getknowledge.modules.courses.tutorial.comments.review.TutorialReview;
import com.getknowledge.modules.courses.tutorial.homeworks.HomeWork;
import com.getknowledge.modules.courses.tutorial.test.Test;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoRepository;
import com.getknowledge.modules.video.Video;
import com.getknowledge.platform.annotations.*;
import com.getknowledge.platform.annotations.Access;
import com.getknowledge.platform.base.entities.*;
import com.getknowledge.platform.modules.user.User;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tutorial")
@ModuleInfo(repositoryName = "TutorialRepository" , serviceName = "TutorialService")
public class Tutorial  extends AbstractEntity implements CloneableEntity<Tutorial> {

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "tutorial")
    private List<HomeWork> homeWorks = new ArrayList<>();

    //Порядковый номер
    @Column(name = "order_number" , nullable = false)
    private Integer orderNumber;

    @ManyToOne(optional = false)
    @JsonIgnore
    private Course course;

    @Column(columnDefinition = "Text" , name = "data")
    @JsonIgnore
    private String data;

    @OneToOne
    @JsonIgnore
    private Video video;

    @ManyToOne(optional = false)
    private Rating avgTutorialRating;

    @Column(name = "last_change_time" , nullable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    private Calendar lastChangeTime;

    @OneToOne
    @Access(myself = true)
    private Tutorial originalTutorial;

    @com.getknowledge.platform.annotations.Access(myself = true)
    @Column(name = "deleting" , nullable = false)
    private Boolean deleting = false;

    @OneToOne
    private Test test;

    @OneToMany(mappedBy = "tutorial")
    @JsonIgnore
    private List<TutorialReview> tutorialReviews = new ArrayList<>();

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public List<HomeWork> getHomeWorks() {
        return homeWorks;
    }

    public void setHomeWorks(List<HomeWork> homeWorks) {
        this.homeWorks = homeWorks;
    }

    public Boolean isDeleting() {
        return deleting;
    }

    public void setDeleting(Boolean deleting) {
        this.deleting = deleting;
    }

    public Tutorial getOriginalTutorial() {
        return originalTutorial;
    }

    public void setOriginalTutorial(Tutorial originalTutorial) {
        this.originalTutorial = originalTutorial;
    }

    public Calendar getLastChangeTime() {
        return lastChangeTime;
    }

    public void setLastChangeTime(Calendar lastChangeTime) {
        this.lastChangeTime = lastChangeTime;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Rating getAvgTutorialRating() {
        return avgTutorialRating;
    }

    public void setAvgTutorialRating(Rating avgTutorialRating) {
        this.avgTutorialRating = avgTutorialRating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = null;
        if (course != null)
            authorizationList = course.getAuthorizationList();
        if (authorizationList != null) {
            if (!course.isBase()) {
                authorizationList.allowReadEveryOne = false;
            }
        }
        return authorizationList;
    }

    @Override
    public Tutorial clone() {
        Tutorial tutorial = new Tutorial();
        tutorial.setId(this.getId());
        tutorial.setCourse(this.getCourse());
        tutorial.setData(this.getData());
        tutorial.setOrderNumber(this.getOrderNumber());
        tutorial.setVideo(this.getVideo());
        tutorial.setLastChangeTime(this.getLastChangeTime());
        tutorial.setOrderNumber(this.getOrderNumber());
        tutorial.setObjectVersion(this.getObjectVersion());
        return tutorial;
    }
}
