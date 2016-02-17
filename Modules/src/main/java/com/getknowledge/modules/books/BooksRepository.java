package com.getknowledge.modules.books;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("BooksRepository")
public class BooksRepository extends BaseRepository<Books> {

    @Override
    protected Class<Books> getClassEntity() {
        return Books.class;
    }
}
