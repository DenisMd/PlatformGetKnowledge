package com.getknowledge.modules.books.tags;

import com.getknowledge.modules.tags.TagRepository;
import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("BooksTagRepository")
public class BooksTagRepository extends TagRepository<BooksTag> {

    @Override
    protected Class<BooksTag> getClassEntity() {
        return BooksTag.class;
    }

    @Override
    protected BooksTag createTag() {
        return new BooksTag();
    }

    @Override
    public void removeUnusedTags(){
       List<BooksTag> list = entityManager.createQuery("select t from BooksTag  t where t.books is empty").getResultList();
       list.forEach((tag -> remove(tag.getId())));
    }
}
