package com.getknowledge.modules.courses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.courses.changelist.ChangeList;
import com.getknowledge.modules.courses.group.GroupCourses;
import com.getknowledge.modules.courses.raiting.Rating;
import com.getknowledge.modules.courses.tags.CoursesTag;
import com.getknowledge.modules.courses.tutorial.Tutorial;
import com.getknowledge.modules.courses.version.Version;
import com.getknowledge.modules.dictionaries.knowledge.Knowledge;
import com.getknowledge.modules.dictionaries.language.Language;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.video.Video;
import com.getknowledge.platform.annotations.*;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.base.entities.CloneableEntity;
import com.getknowledge.platform.base.entities.EntityWithTags;
import com.getknowledge.platform.base.entities.IUser;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.permission.names.PermissionNames;
import com.getknowledge.platform.modules.user.User;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Entity
@Table(name = "course")
@ModuleInfo(repositoryName = "CourseRepository" ,serviceName = "CourseService")
public class Course extends CloneableEntity<Course> implements IUser,EntityWithTags<CoursesTag>{

    private String name;

    private String description;

    @ManyToOne(optional = false)
    private GroupCourses groupCourses;

    @ManyToOne(optional = false)
    private Language language;

    @ManyToOne(optional = false)
    private UserInfo author;

    @ManyToMany(mappedBy = "courses", cascade = {CascadeType.PERSIST})
    private List<CoursesTag> tags = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "courses_source_knowledges")
    private List<Knowledge> sourceKnowledge = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "courses_required_knowledges")
    private List<Knowledge> requiredKnowledge = new ArrayList<>();

    @Column(name = "create_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createDate;

    private Boolean release = false;

    @OneToOne
    @com.getknowledge.platform.annotations.Access(myself = true)
    @JsonIgnore
    private Course baseCourse;

    @OneToOne
    @com.getknowledge.platform.annotations.Access(myself = true)
    private Course draftCourse;

    @OneToOne
    private Video intro;

    @Embedded
    private Version version;

    @Transient
    private Rating rating;

    @OneToMany(mappedBy = "course")
    @JsonIgnore
    private List<Tutorial> tutorials = new ArrayList<>();

    @Basic(fetch= FetchType.LAZY)
    @Lob @Column(name="cover")
    @JsonIgnore
    private byte [] cover;

    @Column(name = "is_base" , columnDefinition = "boolean default true")
    private Boolean base = false;

    @OneToMany(mappedBy = "course" , cascade = {CascadeType.REMOVE})
    private List<ChangeList> changeLists = new ArrayList<>();

    public List<ChangeList> getChangeLists() {
        return changeLists;
    }

    public void setChangeLists(List<ChangeList> changeLists) {
        this.changeLists = changeLists;
    }

    public Course getDraftCourse() {
        return draftCourse;
    }

    public void setDraftCourse(Course draftCourse) {
        this.draftCourse = draftCourse;
    }

    public Calendar getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Calendar createDate) {
        this.createDate = createDate;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public Course getBaseCourse() {
        return baseCourse;
    }

    public void setBaseCourse(Course baseCourse) {
        this.baseCourse = baseCourse;
    }

    public Boolean isRelease() {
        return release;
    }

    public void setRelease(Boolean release) {
        this.release = release;
    }

    public byte[] getCover() {
        return cover;
    }

    public void setCover(byte[] cover) {
        this.cover = cover;
    }

    public GroupCourses getGroupCourses() {
        return groupCourses;
    }

    public void setGroupCourses(GroupCourses groupCourses) {
        this.groupCourses = groupCourses;
    }

    public UserInfo getAuthor() {
        return author;
    }

    public void setAuthor(UserInfo author) {
        this.author = author;
    }

    public List<CoursesTag> getTags() {
        return tags;
    }

    public void setTags(List<CoursesTag> coursesTagList) {
        this.tags = coursesTagList;
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
        rating = new Rating();
        int qualityExercises = 0;
        int qualityInformation = 0;
        int qualityTest = 0;
        int relevanceInformation = 0;

        for (Tutorial tutorial : tutorials) {
            qualityExercises += tutorial.getAvgTutorialRating().getQualityExercises();
            qualityInformation += tutorial.getAvgTutorialRating().getQualityInformation();
            qualityTest += tutorial.getAvgTutorialRating().getQualityTest();
            relevanceInformation += tutorial.getAvgTutorialRating().getRelevanceInformation();
        }

        if (tutorials.size() != 0 && qualityExercises != 0) {
            rating.setQualityExercises(qualityExercises / tutorials.size());
            rating.setQualityInformation(qualityInformation / tutorials.size());
            rating.setQualityTest(qualityTest / tutorials.size());
            rating.setRelevanceInformation(relevanceInformation / tutorials.size());
        }

        return rating;
    }


    public Boolean isBase() {
        return base;
    }

    public void setBase(Boolean base) {
        this.base = base;
    }

    @Override
    public boolean isContinueIfNotEnoughRights() {
        return true;
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

    public List<Tutorial> getTutorials() {
        return tutorials;
    }

    public void setTutorials(List<Tutorial> tutorials) {
        this.tutorials = tutorials;
    }

    @Override
    public Course clone() {
        Course course = new Course();
        course.setId(this.getId());
        course.setAuthor(this.getAuthor());
        course.setBaseCourse(this.getBaseCourse());
        course.setTags(this.getTags());
        course.setCover(this.getCover());
        course.setDescription(this.getDescription());
        course.setGroupCourses(this.getGroupCourses());
        course.setIntro(this.getIntro());
        course.setLanguage(this.getLanguage());
        course.setName(this.getName());
        course.setRelease(this.isRelease());
        course.setRequiredKnowledge(this.getRequiredKnowledge());
        course.setSourceKnowledge(this.getSourceKnowledge());
        course.setVersion(this.getVersion());
        course.setTutorials(this.getTutorials());
        course.setBase(this.isBase());
        course.setCreateDate(this.getCreateDate());
        course.setChangeLists(this.getChangeLists());
        return course;
    }

    @Override
    public User getUser() {
        return author == null ? null : author.getUser();
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();

        if (base)
            authorizationList.allowReadEveryOne = true;

        authorizationList.allowCreateEveryOne = false;
        authorizationList.getPermissionsForCreate().add(new Permission(PermissionNames.CreateCourse));
        authorizationList.getPermissionsForRemove().add(new Permission(PermissionNames.EditCourse));

        //Если курс имеет состояние release даже автор его не может редоктировать
        if (!release)
            authorizationList.getPermissionsForEdit().add(new Permission(PermissionNames.EditCourse));

        if (author != null && !release)
            authorizationList.getUserList().add(author.getUser());

        return authorizationList;
    }
}
