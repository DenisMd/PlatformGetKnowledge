package com.getknowledge.modules.books.tags;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.books.Book;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.base.entities.ITag;

import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "book_tag")
public class BooksTag extends AbstractEntity implements ITag {

    private String tagName;

    @ManyToMany
    @JoinTable(name = "tags_books")
    @JsonIgnore
    private List<Book> books = new ArrayList<>();


    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName.toLowerCase();
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
