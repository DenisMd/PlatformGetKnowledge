package com.getknowledge.modules.books.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.books.Book;
import com.getknowledge.modules.messages.Comment;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.permission.names.PermissionNames;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "book_comment")
@ModuleInfo(repositoryName = "BookCommentRepository" , serviceName = "BookCommentService")
public class BookComment extends Comment {

    @ManyToOne(optional = false)
    @JsonIgnore
    private Book book;

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.getPermissionsForEdit().add(new Permission(PermissionNames.BlockComments));
        return authorizationList;
    }

    @Override
    public AbstractEntity clone() {
        BookComment bookComment = new BookComment();
        bookComment.setCommentStatus(this.getCommentStatus());
        bookComment.setCreateTime(this.getCreateTime());
        bookComment.setMessage(this.getMessage());
        bookComment.setSender(this.getSender());
        bookComment.setId(this.getId());
        bookComment.setObjectVersion(this.getObjectVersion());
        return bookComment;
    }
}
