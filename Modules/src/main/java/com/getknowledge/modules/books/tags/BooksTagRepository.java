package com.getknowledge.modules.books.tags;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("BooksTagRepository")
public class BooksTagRepository extends BaseRepository<BooksTag> implements ITagRepository<BooksTag> {

    @Override
    protected Class<BooksTag> getClassEntity() {
        return BooksTag.class;
    }

    @Override
    public void removeTagsFromEntity(EntityWithTags<BooksTag> entity) {
        for (BooksTag booksTag : entity.getTags()) {
            booksTag.getBooks().remove(entity);
            merge(booksTag);
        }

        entity.getTags().clear();
    }

    @Override
    public void createTags(List<String> tags, EntityWithTags<BooksTag> entity) {
        for (String tag : tags) {
            BooksTag booksTag = createIfNotExist(tag);
            entity.getTags().add(booksTag);
        }
    }

    @Override
    public BooksTag createIfNotExist(String tag) {
        BooksTag result = getSingleEntityByFieldAndValue("tagName" , tag.toLowerCase());

        if (result == null) {
            result = new BooksTag();
            result.setTagName(tag.toLowerCase());
            create(result);
        }

        return result;
    }

    @Override
    public void removeUnusedTags(){
       List<BooksTag> list = entityManager.createQuery("select t from BooksTag  t where t.books is empty").getResultList();
       list.forEach((tag -> {
           remove(tag.getId());
       }));
    }
}
