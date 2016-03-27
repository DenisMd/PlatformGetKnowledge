package com.getknowledge.modules.userInfo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.courses.Course;
import com.getknowledge.modules.dictionaries.city.City;
import com.getknowledge.modules.dictionaries.language.Language;
import com.getknowledge.modules.menu.Menu;
import com.getknowledge.modules.userInfo.post.messages.PostMessage;
import com.getknowledge.modules.userInfo.socialLink.UserSocialLink;
import com.getknowledge.platform.annotations.Access;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.base.entities.IUser;
import com.getknowledge.platform.base.entities.CloneableEntity;
import com.getknowledge.platform.modules.user.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Entity
@Table(name = "user_info")
@ModuleInfo(repositoryName = "UserInfoRepository" , serviceName = "UserInfoService")
public class UserInfo  extends CloneableEntity<UserInfo> implements IUser{

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(length = 40)
    private String specialty;

    @Column(name = "birth_day")
    private Calendar birthDay;

    @Basic(fetch=FetchType.LAZY)
    @Lob @Column(name="image")
    @JsonIgnore
    private byte[] profileImage;

    @Access(myself = true)
    @ManyToOne
    private Language language;

    @ManyToOne
    private City city;

    @Access(roles = {"ROLE_ADMIN"})
    @OneToOne
    private User user;

    @Column(name = "man")
    private Boolean man;

    @Access(myself = true)
    private Boolean firstLogin;

    @Column(length = 120)
    private String status;

    @Column(length = 500)
    private String webSite;

    @Embedded
    private UserSocialLink links;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "user_frieds")
    private List<UserInfo> friends = new ArrayList<>();

    @Transient
    private Menu userMenu;

    //Список курсов, которые изучает данный пользователь
    @ManyToMany
    @JsonIgnore
    @JoinTable(name = "users_studied_courses")
    private List<Course> studiedCourses = new ArrayList<>();

    @Transient
    private boolean online = false;

    @ManyToMany
    @JsonIgnore
    @JoinTable(name = "users_purchased_courses")
    private List<Course> purchasedCourses = new ArrayList<>();

    @OneToMany(mappedBy = "recipient")
    @JsonIgnore
    private List<PostMessage> posts = new ArrayList<>();

    public List<PostMessage> getPosts() {
        return posts;
    }

    public void setPosts(List<PostMessage> recipmentPosts) {
        this.posts = recipmentPosts;
    }

    public List<Course> getPurchasedCourses() {
        return purchasedCourses;
    }

    public void setPurchasedCourses(List<Course> purchasedCourses) {
        this.purchasedCourses = purchasedCourses;
    }

    public List<Course> getStudiedCourses() {
        return studiedCourses;
    }

    public void setStudiedCourses(List<Course> studiedCourses) {
        this.studiedCourses = studiedCourses;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public Menu getUserMenu() {
        return userMenu;
    }

    public void setUserMenu(Menu userMenu) {
        this.userMenu = userMenu;
    }

    public UserSocialLink getLinks() {
        return links;
    }

    public void setLinks(UserSocialLink links) {
        this.links = links;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFirstLogin(Boolean firstLogin) {
        this.firstLogin = firstLogin;
    }

    public void setMan(Boolean man) {
        this.man = man;
    }

    public Boolean getFirstLogin() {
        return firstLogin;
    }

    public Boolean getMan() {
        return man;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public byte[] getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<UserInfo> getFriends() {
        return friends;
    }

    public void setFriends(List<UserInfo> friends) {
        this.friends = friends;
    }

    @Override
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Calendar getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Calendar birthDay) {
        this.birthDay = birthDay;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.allowCreateEveryOne = false;
        authorizationList.allowReadEveryOne = true;
        return authorizationList;
    }

    @Override
    public UserInfo clone() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(this.getId());
        userInfo.setBirthDay(this.getBirthDay());
        userInfo.setFirstName(this.getFirstName());
        userInfo.setLanguage(this.getLanguage());
        userInfo.setLastName(this.getLastName());
        userInfo.setSpecialty(this.getSpecialty());
        userInfo.setUser(this.getUser());
        userInfo.setMan(this.getMan());
        userInfo.setFirstLogin(this.getFirstLogin());
        userInfo.setStatus(this.getStatus());
        userInfo.setCity(this.getCity());
        userInfo.setWebSite(this.webSite);
        userInfo.setLinks(this.links);
        userInfo.setUserMenu(this.userMenu);
        userInfo.setOnline(this.online);
        return userInfo;
    }
}
