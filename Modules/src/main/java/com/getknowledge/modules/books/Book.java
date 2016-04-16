package com.getknowledge.modules.books;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.books.group.GroupBooks;
import com.getknowledge.modules.books.tags.BooksTag;
import com.getknowledge.modules.dictionaries.language.Language;
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
@Table(name = "book")
@ModuleInfo(repositoryName = "BookRepository" , serviceName = "BookService")
public class Book extends AbstractEntity implements CloneableEntity<Book>,IUser, EntityWithTags<BooksTag>{

    private String name;

    @Column(length = 1000)
    private String description;

    @ManyToOne(optional = false)
    private GroupBooks groupBooks;

    @ManyToOne(optional = false)
    private Language language;

    @ManyToOne(optional = false)
    private UserInfo owner;

    @ManyToMany(mappedBy = "books", cascade = {CascadeType.PERSIST})
    private List<BooksTag> tags = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "books_link")
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
    @JsonIgnore
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public GroupBooks getGroupBooks() {
        return groupBooks;
    }

    public void setGroupBooks(GroupBooks groupBooks) {
        this.groupBooks = groupBooks;
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

    @Override
    public List<BooksTag> getTags() {
        return tags;
    }

    @Override
    public void setTags(List<BooksTag> tags) {
        this.tags = tags;
    }

    public UserInfo getOwner() {
        return owner;
    }

    public void setOwner(UserInfo owner) {
        this.owner = owner;
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
        authorizationList.getPermissionsForCreate().add(new Permission(PermissionNames.CreateBooks.getName()));
        authorizationList.getPermissionsForEdit().add(new Permission(PermissionNames.EditBooks.getName()));
        authorizationList.getPermissionsForRemove().add(new Permission(PermissionNames.EditBooks.getName()));
        return authorizationList;
    }

    @Override
    public Book clone() {
        Book cloneBook = new Book();
        cloneBook.setId(this.getId());
        cloneBook.setGroupBooks(this.getGroupBooks());
        cloneBook.setName(this.getName());
        cloneBook.setBookData(this.getBookData());
        cloneBook.setCover(this.getCover());
        cloneBook.setDescription(this.getDescription());
        cloneBook.setLanguage(this.getLanguage());
        cloneBook.setLinks(this.getLinks());
        cloneBook.setOwner(this.getOwner());
        cloneBook.setTags(this.getTags());
        cloneBook.setObjectVersion(this.getObjectVersion());
        return cloneBook;
    }
}
