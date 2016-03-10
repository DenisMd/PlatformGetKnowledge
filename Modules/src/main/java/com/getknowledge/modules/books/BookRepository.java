package com.getknowledge.modules.books;

import com.getknowledge.platform.base.repositories.ProtectedRepository;
import org.springframework.stereotype.Repository;

@Repository("BookRepository")
public class BookRepository extends ProtectedRepository<Book> {

    @Override
    protected Class<Book> getClassEntity() {
        return Book.class;
    }
}
