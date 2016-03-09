package com.getknowledge.modules.courses;

import com.getknowledge.modules.courses.raiting.Rating;
import com.getknowledge.modules.courses.tags.CoursesTag;
import com.getknowledge.modules.dictionaries.knowledge.Knowledge;
import com.getknowledge.modules.dictionaries.language.Language;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.video.Video;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.base.entities.CloneableEntity;
import com.getknowledge.platform.base.entities.IUser;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.permission.names.PermissionNames;
import com.getknowledge.platform.modules.user.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "course")
@ModuleInfo(repositoryName = "CourseRepository" ,serviceName = "CourseService")
public class Course extends CloneableEntity<Course> implements IUser{

    private String name;

    private String description;

    @ManyToOne(optional = false)
    private Language language;

    @ManyToOne(optional = false)
    private UserInfo author;

    @ManyToMany(mappedBy = "courseList" , cascade = {CascadeType.PERSIST})
    private List<CoursesTag> coursesTagList;

    @Column(name = "is_base")
    private Boolean base = true;

    @OneToMany
    private List<Knowledge> sourceKnowledge = new ArrayList<>();

    @OneToMany
    private List<Knowledge> requiredKnowledge = new ArrayList<>();

    @OneToOne
    private Video intro;

    @Transient
    private Rating rating;

    public UserInfo getAuthor() {
        return author;
    }

    public void setAuthor(UserInfo author) {
        this.author = author;
    }

    public Boolean getBase() {
        return base;
    }

    public void setBase(Boolean base) {
        this.base = base;
    }

    public List<CoursesTag> getCoursesTagList() {
        return coursesTagList;
    }

    public void setCoursesTagList(List<CoursesTag> coursesTagList) {
        this.coursesTagList = coursesTagList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Video getIntro() {
        return intro;
    }

    public void setIntro(Video intro) {
        this.intro = intro;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Rating getRating() {
        //TODO: вычислить средний рейтинг исходя из туториалов
        return rating;
    }

    public List<Knowledge> getRequiredKnowledge() {
        return requiredKnowledge;
    }

    public void setRequiredKnowledge(List<Knowledge> requiredKnowledge) {
        this.requiredKnowledge = requiredKnowledge;
    }

    public List<Knowledge> getSourceKnowledge() {
        return sourceKnowledge;
    }

    public void setSourceKnowledge(List<Knowledge> sourceKnowledge) {
        this.sourceKnowledge = sourceKnowledge;
    }

    @Override
    public Course clone() {
        Course course = new Course();
        course.setId(this.getId());
        return course;
    }

    @Override
    public User getUser() {
        return author.getUser();
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.allowReadEveryOne = true;
        authorizationList.allowCreateEveryOne = false;
        authorizationList.getPermissionsForCreate().add(new Permission(PermissionNames.CreateCourse));
        authorizationList.getPermissionsForEdit().add(new Permission(PermissionNames.EditCourse));
        authorizationList.getPermissionsForRemove().add(new Permission(PermissionNames.EditCourse));
        return authorizationList;
    }
}
