package com.getknowledge.modules.books;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.books.group.GroupBooks;
import com.getknowledge.modules.books.tags.BooksTag;
import com.getknowledge.modules.dictionaries.language.Language;
import com.getknowledge.modules.attachements.FileAttachment;
import com.getknowledge.modules.platform.auth.PermissionNames;
import com.getknowledge.modules.tags.EntityWithTags;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.platform.annotations.ModelView;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.annotations.ViewType;
import com.getknowledge.platform.base.entities.*;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.user.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Entity
@Table(name = "book")
@ModuleInfo(repositoryName = "BookRepository" , serviceName = "BookService")
public class Book extends AbstractEntity implements CloneableEntity<Book>,IUser, EntityWithTags<BooksTag> {

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @ManyToOne(optional = false)
    @ModelView(type = {ViewType.Public})
    private GroupBooks groupBooks;

    @ManyToOne(optional = false)
    private Language language;

    @ManyToOne(optional = false)
    @ModelView(type = {ViewType.CompactPublic})
    private UserInfo owner;

    @ManyToMany(mappedBy = "books", cascade = {CascadeType.PERSIST})
    private List<BooksTag> tags = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "books_link")
    @Column(name = "links", length = 1000)
    private List<String> links = new ArrayList<>();

    @Column(name = "create_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createDate;

    @Basic(fetch=FetchType.LAZY)
    @Lob @Column(name="cover")
    @JsonIgnore
    private byte[] cover;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private FileAttachment fileAttachment;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "author_name")
    private String authorName;

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Calendar getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Calendar createDate) {
        this.createDate = createDate;
    }

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

    public FileAttachment getFileAttachment() {
        return fileAttachment;
    }

    public void setFileAttachment(FileAttachment fileAttachment) {
        this.fileAttachment = fileAttachment;
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
        authorizationList.getPermissionsForCreate().add(new Permission(PermissionNames.CreateBooks()));
        authorizationList.getPermissionsForEdit().add(new Permission(PermissionNames.EditBooks()));
        authorizationList.getPermissionsForRemove().add(new Permission(PermissionNames.EditBooks()));
        return authorizationList;
    }

    @Override
    public Book clone() {
        Book cloneBook = new Book();
        cloneBook.setId(this.getId());
        cloneBook.setGroupBooks(this.getGroupBooks());
        cloneBook.setAuthorName(this.getAuthorName());
        cloneBook.setName(this.getName());
        cloneBook.setCover(this.getCover());
        cloneBook.setDescription(this.getDescription());
        cloneBook.setLanguage(this.getLanguage());
        cloneBook.setLinks(this.getLinks());
        cloneBook.setOwner(this.getOwner());
        cloneBook.setTags(this.getTags());
        cloneBook.setCreateDate(this.createDate);
        cloneBook.setObjectVersion(this.getObjectVersion());
        cloneBook.setFileName(this.getFileName());
        return cloneBook;
    }
}
