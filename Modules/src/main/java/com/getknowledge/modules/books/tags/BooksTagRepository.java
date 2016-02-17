package com.getknowledge.modules.books.tags;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("BooksTagRepository")
public class BooksTagRepository extends BaseRepository<BooksTag> {


    @Override
    protected Class<BooksTag> getClassEntity() {
        return BooksTag.class;
    }

    public BooksTag createIfNotExist(String tag) {
        BooksTag result = getSingleEntityByFieldAndValue("tagName" , tag);

        if (result == null) {
            result = new BooksTag();
            result.setTagName(tag);
            create(result);
        }

        return result;
    }
}
