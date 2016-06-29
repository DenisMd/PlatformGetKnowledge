package com.getknowledge.modules.books.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.books.Book;
import com.getknowledge.modules.section.Section;
import com.getknowledge.platform.annotations.ModelView;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.annotations.ViewType;
import com.getknowledge.platform.base.entities.CloneableEntity;
import com.getknowledge.modules.abs.entities.Folder;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("books")
@ModuleInfo(repositoryName = "GroupBooksRepository" , serviceName = "GroupBooksService")
public class GroupBooks extends Folder {

    @Transient
    private long booksCount = 0;

    @OneToMany(mappedBy = "groupBooks")
    @JsonIgnore
    private List<Book> books;

    public long getBooksCount() {
        return booksCount;
    }

    public void setBooksCount(long booksCount) {
        this.booksCount = booksCount;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    @Override
    public Folder cloneFolder() {
        GroupBooks groupBooks = new GroupBooks();
        groupBooks.setBooksCount(getBooksCount());
        return groupBooks;
    }
}
