package com.getknowledge.modules.books;

import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import org.springframework.stereotype.Repository;

@Repository("BooksRepository")
public class BooksRepository extends ProtectedRepository<Books> {

    @Override
    protected Class<Books> getClassEntity() {
        return Books.class;
    }
}
