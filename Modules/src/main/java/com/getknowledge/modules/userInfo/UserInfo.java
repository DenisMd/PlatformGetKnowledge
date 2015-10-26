package com.getknowledge.modules.userInfo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.getknowledge.platform.annotations.Access;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.modules.user.User;

import javax.persistence.*;

@Entity
@Table(name = "user_info")
@ModuleInfo(repositoryName = "UserInfoRepository" , serviceName = "UserInfoService")
public class UserInfo  extends AbstractEntity{

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(length = 40)
    private String specialty;

    @Basic(fetch=FetchType.LAZY)
    @Lob @Column(name="image")
    private byte[] profileImage;

    @Access(roles = {"ROLE_ADMIN"})
    @OneToOne
    private User user;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.allowCreateEveryOne = false;
        return authorizationList;
    }
}
