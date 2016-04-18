package com.getknowledge.modules.courses.tutorial.homeworks;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.courses.tutorial.Tutorial;
import com.getknowledge.modules.video.Video;
import com.getknowledge.platform.annotations.*;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.base.entities.CloneableEntity;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Table(name = "home_work")
@ModuleInfo(repositoryName = "HomeWorkRepository" , serviceName = "HomeWorkService")
public class HomeWork extends AbstractEntity implements CloneableEntity<HomeWork> {

    @ManyToOne(optional = false)
    @JsonIgnore
    private Tutorial tutorial;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "Text" , name = "data")
    @JsonIgnore
    private String data;

    @OneToOne
    @JsonIgnore
    private Video video;

    @Column(name = "last_change_time" , nullable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    private Calendar lastChangeTime;

    @OneToOne
    @com.getknowledge.platform.annotations.Access(myself = true)
    private HomeWork original;

    @JsonIgnore
    @Column(name = "deleting" , nullable = false)
    private Boolean deleting = false;

    public Calendar getLastChangeTime() {
        return lastChangeTime;
    }

    public void setLastChangeTime(Calendar lastChangeTime) {
        this.lastChangeTime = lastChangeTime;
    }

    public HomeWork getOriginal() {
        return original;
    }

    public void setOriginal(HomeWork original) {
        this.original = original;
    }

    public boolean isDeleting() {
        return deleting;
    }

    public void setDeleting(boolean deleting) {
        this.deleting = deleting;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public Tutorial getTutorial() {
        return tutorial;
    }

    public void setTutorial(Tutorial tutorial) {
        this.tutorial = tutorial;
    }

    @Override
    public HomeWork clone() {
        HomeWork homeWork = new HomeWork();
        homeWork.setName(getName());
        homeWork.setId(this.getId());
        homeWork.setObjectVersion(this.getObjectVersion());
        return homeWork;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        if (tutorial != null)
            return tutorial.getAuthorizationList();
        return null;
    }
}
