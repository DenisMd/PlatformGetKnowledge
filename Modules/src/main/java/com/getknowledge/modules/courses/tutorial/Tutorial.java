package com.getknowledge.modules.courses.tutorial;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.courses.Course;
import com.getknowledge.modules.courses.raiting.Rating;
import com.getknowledge.modules.video.Video;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.*;

@Entity
@Table(name = "tutorial")
@ModuleInfo(repositoryName = "TutorialRepository" , serviceName = "TutorialService")
public class Tutorial extends AbstractEntity {

    private String name;

    @Column(length = 500)
    private String description;

    //Порядковый номер
    @Column(name = "order_number")
    private int orderNumber;

    @ManyToOne
    @JsonIgnore
    private Course course;

    @Column(columnDefinition = "Text" , name = "data")
    private String data;

    @Basic(fetch= FetchType.LAZY)
    @Lob @Column(name="cover")
    @JsonIgnore
    private byte [] cover;

    @OneToOne
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

    public byte[] getCover() {
        return cover;
    }

    public void setCover(byte[] cover) {
        this.cover = cover;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return null;
    }
}
