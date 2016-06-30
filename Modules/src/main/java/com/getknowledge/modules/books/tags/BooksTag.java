package com.getknowledge.modules.books.tags;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.books.Book;
import com.getknowledge.modules.tags.EntityWithTags;
import com.getknowledge.modules.tags.Tag;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("book")
public class BooksTag extends Tag {

    @ManyToMany
    @JoinTable(name = "tags_books")
    @JsonIgnore
    private List<Book> books = new ArrayList<>();

    @Override
    public List<EntityWithTags> getEntities() {
        return (List<EntityWithTags>)(List<?>)books;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
