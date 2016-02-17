package com.getknowledge.modules.books.tags;

import com.getknowledge.modules.books.Books;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "book_tag")
public class BooksTag extends AbstractEntity {

    private String tagName;

    @ManyToMany
    @JoinTable(name = "tags_books")
    private List<Books> books = new ArrayList<>();


    public List<Books> getBooks() {
        return books;
    }

    public void setBooks(List<Books> books) {
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
