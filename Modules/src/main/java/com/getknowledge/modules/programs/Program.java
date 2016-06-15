package com.getknowledge.modules.programs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.books.group.GroupBooks;
import com.getknowledge.modules.books.tags.BooksTag;
import com.getknowledge.modules.dictionaries.language.Language;
import com.getknowledge.modules.programs.group.GroupPrograms;
import com.getknowledge.modules.programs.tags.ProgramTag;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.*;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.permission.names.PermissionNames;
import com.getknowledge.platform.modules.user.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "program")
@ModuleInfo(repositoryName = "ProgramRepository" , serviceName = "ProgramService")
public class Program extends AbstractEntity implements CloneableEntity<Program>,IUser,EntityWithTags<ProgramTag> {

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @ManyToOne(optional = false)
    private GroupPrograms groupPrograms;

    @ManyToOne(optional = false)
    private Language language;

    @ManyToOne(optional = false)
    private UserInfo owner;

    @ManyToMany(mappedBy = "programs", cascade = {CascadeType.PERSIST})
    private List<ProgramTag> tags = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "programs_link")
    @Column(name = "links")
    private List<String> links = new ArrayList<>();


    @Basic(fetch=FetchType.LAZY)
    @Lob @Column(name="cover")
    @JsonIgnore
    private byte[] cover;

    @Basic(fetch=FetchType.LAZY)
    @Lob @Column(name="data")
    @JsonIgnore
    private byte[] bookData;

    @Column(name = "file_name")
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public byte[] getCover() {
        return cover;
    }

    public void setCover(byte[] cover) {
        this.cover = cover;
    }

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

    public UserInfo getOwner() {
        return owner;
    }

    public void setOwner(UserInfo owner) {
        this.owner = owner;
    }


    public GroupPrograms getGroupPrograms() {
        return groupPrograms;
    }

    public void setGroupPrograms(GroupPrograms groupPrograms) {
        this.groupPrograms = groupPrograms;
    }

    @Override
    public List<ProgramTag> getTags() {
        return tags;
    }

    @Override
    public void setTags(List<ProgramTag> tags) {
        this.tags = tags;
    }

    @Override
    public User getUser() {
        return owner.getUser();
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.allowReadEveryOne = true;

        if (owner != null)
            authorizationList.getUserList().add(owner.getUser());
        authorizationList.getPermissionsForCreate().add(new Permission(PermissionNames.CreatePrograms.getName()));
        authorizationList.getPermissionsForEdit().add(new Permission(PermissionNames.EditPrograms.getName()));
        authorizationList.getPermissionsForRemove().add(new Permission(PermissionNames.EditPrograms.getName()));
        return authorizationList;
    }

    @Override
    public Program clone() {
        Program cloneProgram = new Program();
        cloneProgram.setId(this.getId());
        cloneProgram.setGroupPrograms(this.getGroupPrograms());
        cloneProgram.setName(this.getName());
        cloneProgram.setBookData(this.getBookData());
        cloneProgram.setCover(this.getCover());
        cloneProgram.setDescription(this.getDescription());
        cloneProgram.setLanguage(this.getLanguage());
        cloneProgram.setLinks(this.getLinks());
        cloneProgram.setOwner(this.getOwner());
        cloneProgram.setTags(this.getTags());
        cloneProgram.setObjectVersion(this.getObjectVersion());
        cloneProgram.setFileName(this.getFileName());
        return cloneProgram;
    }
}
