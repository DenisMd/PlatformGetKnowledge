package com.getknowledge.modules.courses.tutorial;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.courses.Course;
import com.getknowledge.modules.courses.raiting.Rating;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoRepository;
import com.getknowledge.modules.video.Video;
import com.getknowledge.platform.annotations.*;
import com.getknowledge.platform.base.entities.*;
import com.getknowledge.platform.modules.user.User;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "tutorial")
@ModuleInfo(repositoryName = "TutorialRepository" , serviceName = "TutorialService")
public class Tutorial  extends CloneableEntity<Tutorial> implements IOwner{

    private String name;

    //Порядковый номер
    @Column(name = "order_number")
    private int orderNumber;

    @ManyToOne
    @JsonIgnore
    private Course course;

    @Column(columnDefinition = "Text" , name = "data")
    @com.getknowledge.platform.annotations.Access(forOwners = true)
    private String data;

    @OneToOne
    @com.getknowledge.platform.annotations.Access(forOwners = true)
    private Video video;

    @Transient
    private Rating avgTutorialRating;

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
        //TODO:Вычислить из sql запроса
        avgTutorialRating = new Rating();
        return avgTutorialRating;
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
        if (course != null)
            return course.getAuthorizationList();
        return null;
    }

    @Override
    public Tutorial clone() {
        Tutorial tutorial = new Tutorial();
        tutorial.setId(this.getId());
        tutorial.setCourse(this.getCourse());
        tutorial.setData(this.getData());
        tutorial.setOrderNumber(this.getOrderNumber());
        tutorial.setVideo(this.getVideo());
        return tutorial;
    }

    @Autowired
    @Transient
    @JsonIgnore
    private UserInfoRepository userInfoRepository;

    @Override
    public boolean isOwner(User user) {

        if (user != null && Objects.equals(course.getAuthor().getUser().getId(), user.getId()))
            return true;

        if (course.isBase())
            return true;


        UserInfo userInfo = userInfoRepository.getUserInfoByUser(user);
        if (userInfo != null) {
            if (userInfo.getStudiedCourses().contains(course)) {
                return true;
            }

            if (userInfo.getPurchasedCourses().contains(course)) {
                return true;
            }
        }

        return false;
    }
}
