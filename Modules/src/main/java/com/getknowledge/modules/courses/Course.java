package com.getknowledge.modules.courses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.books.Book;
import com.getknowledge.modules.courses.changelist.ChangeList;
import com.getknowledge.modules.courses.group.GroupCourses;
import com.getknowledge.modules.courses.questions.CourseQuestion;
import com.getknowledge.modules.courses.raiting.Rating;
import com.getknowledge.modules.courses.tags.CoursesTag;
import com.getknowledge.modules.courses.tutorial.Tutorial;
import com.getknowledge.modules.courses.version.Version;
import com.getknowledge.modules.dictionaries.knowledge.Knowledge;
import com.getknowledge.modules.dictionaries.language.Language;
import com.getknowledge.modules.platform.auth.PermissionNames;
import com.getknowledge.modules.programs.Program;
import com.getknowledge.modules.shop.item.Item;
import com.getknowledge.modules.tags.EntityWithTags;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.video.Video;
import com.getknowledge.platform.annotations.*;
import com.getknowledge.platform.base.entities.*;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.user.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Entity
@Table(name = "course")
@ModuleInfo(repositoryName = "CourseRepository" ,serviceName = "CourseService")
public class Course extends AbstractEntity implements CloneableEntity<Course>,IUser,EntityWithTags<CoursesTag> {

    @Column(nullable = false)
    private String name;

    @Column(length = 1500)
    private String description;

    @Column(name = "draft", nullable = false)
    private Boolean draft = false;

    @ManyToOne(optional = false)
    @ModelView(type = {ViewType.Public})
    private GroupCourses groupCourses;

    @ManyToOne(optional = false)
    private Language language;

    @ManyToOne(optional = false)
    @ModelView(type = ViewType.CompactPublic)
    private UserInfo author;

    @ManyToMany(mappedBy = "courses", cascade = {CascadeType.PERSIST})
    private List<CoursesTag> tags = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "courses_source_knowledges")
    private List<Knowledge> sourceKnowledge = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "courses_required_knowledges")
    private List<Knowledge> requiredKnowledge = new ArrayList<>();

    @Column(name = "create_date" , nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createDate;

    @Column(name = "last_released_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar lastReleasedDate;

    @ManyToMany
    @JsonIgnore
    private List<UserInfo> testers = new ArrayList<>();

    @Column(nullable = false)
    private Boolean release = false;

    @OneToOne
    @JsonIgnore
    private Course baseCourse;

    @OneToOne
    @com.getknowledge.platform.annotations.Access(myself = true)
    private Course draftCourse;

    @OneToOne
    private Video intro;

    @Embedded
    private Version version;

    @ManyToOne(optional = false)
    private Rating rating;

    @OneToMany(mappedBy = "course")
    @JsonIgnore
    private List<Tutorial> tutorials = new ArrayList<>();

    @Basic(fetch= FetchType.LAZY)
    @Lob @Column(name="cover")
    @JsonIgnore
    private byte [] cover;

    @OneToOne
    private Item item;

    @Column(name = "is_base" , columnDefinition = "boolean default true" , nullable = false)
    private Boolean base = false;

    @OneToMany(mappedBy = "course" , cascade = {CascadeType.REMOVE})
    private List<ChangeList> changeLists = new ArrayList<>();

    @ManyToMany
    @JsonIgnore
    private List<Book> books = new ArrayList<>();

    @ManyToMany
    @JsonIgnore
    private List<Program> programs = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    @JsonIgnore
    private List<CourseQuestion> questions = new ArrayList<>();

    public List<CourseQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<CourseQuestion> questions) {
        this.questions = questions;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public List<Program> getPrograms() {
        return programs;
    }

    public void setPrograms(List<Program> programs) {
        this.programs = programs;
    }

    public List<UserInfo> getTesters() {
        return testers;
    }

    public void setTesters(List<UserInfo> testers) {
        this.testers = testers;
    }

    public Calendar getLastReleasedDate() {
        return lastReleasedDate;
    }

    public void setLastReleasedDate(Calendar lastReleasedDate) {
        this.lastReleasedDate = lastReleasedDate;
    }

    public Boolean getDraft() {
        return draft;
    }

    public void setDraft(Boolean draft) {
        this.draft = draft;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

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

    @Override
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
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
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
        course.setObjectVersion(this.getObjectVersion());
        course.setRating(getRating());
        course.setItem(item);
        return course;
    }

    @Override
    public User getUser() {
        return author == null ? null : author.getUser();
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.allowReadEveryOne = true;
        authorizationList.allowCreateEveryOne = false;
        authorizationList.getPermissionsForCreate().add(new Permission(PermissionNames.CreateCourse()));

        //Если курс имеет состояние release даже автор его не может редоктировать
        if (!release) {
            authorizationList.getPermissionsForRemove().add(new Permission(PermissionNames.EditCourse()));
            authorizationList.getPermissionsForEdit().add(new Permission(PermissionNames.EditCourse()));
            if (author != null) {
                authorizationList.getUserList().add(author.getUser());
            }
        }

        return authorizationList;
    }
}
