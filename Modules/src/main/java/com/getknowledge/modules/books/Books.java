package com.getknowledge.modules.books;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.books.tags.BooksTag;
import com.getknowledge.modules.dictionaries.language.Language;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.permission.names.PermissionNames;
import com.getknowledge.platform.modules.user.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
@ModuleInfo(repositoryName = "BooksRepository" , serviceName = "BooksService")
public class Books extends AbstractEntity{

    private String name;

    private String description;

    @ManyToOne
    private Language language;

    @ManyToOne
    private UserInfo user;

    @ManyToMany(mappedBy = "books")
    private List<BooksTag> tags;

    @ElementCollection
    @CollectionTable(name = "books_link")
    @Column(name = "links")
    private List<String> links = new ArrayList<>();


    @Basic(fetch=FetchType.LAZY)
    @Lob @Column(name="data")
    @JsonIgnore
    private byte[] bookData;

    public byte[] getBookData() {
        return bookData;
    }

    public void setBookData(byte[] bookData) {
        this.bookData = bookData;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BooksTag> getTags() {
        return tags;
    }

    public void setTags(List<BooksTag> tags) {
        this.tags = tags;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.allowReadEveryOne = true;

        authorizationList.getUserList().add(user.getUser());
        authorizationList.getPermissionsForCreate().add(new Permission(PermissionNames.CreateBooks.getName()));
        authorizationList.getPermissionsForEdit().add(new Permission(PermissionNames.EditBooks.getName()));
        authorizationList.getPermissionsForRemove().add(new Permission(PermissionNames.EditBooks.getName()));
        return authorizationList;
    }
}
