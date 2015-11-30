package com.getknowledge.modules.userInfo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.getknowledge.modules.dictionaries.city.City;
import com.getknowledge.modules.dictionaries.language.Language;
import com.getknowledge.modules.userInfo.post.Post;
import com.getknowledge.platform.annotations.Access;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.base.entities.IUser;
import com.getknowledge.platform.modules.user.User;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Table(name = "user_info")
@ModuleInfo(repositoryName = "UserInfoRepository" , serviceName = "UserInfoService")
public class UserInfo  extends AbstractEntity implements IUser {

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

    @OneToOne
    private Post post;

    @Access(myself = true)
    private Boolean firstLogin;


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

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.allowCreateEveryOne = false;
        authorizationList.allowReadEveryOne = true;
        return authorizationList;
    }
}
