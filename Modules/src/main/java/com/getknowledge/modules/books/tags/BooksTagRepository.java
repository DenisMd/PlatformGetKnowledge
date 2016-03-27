package com.getknowledge.modules.books.tags;

import com.getknowledge.platform.base.entities.EntityWithTags;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.ITagRepository;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("BooksTagRepository")
public class BooksTagRepository extends BaseRepository<BooksTag> implements ITagRepository<BooksTag> {

    @Autowired
    private TraceService trace;

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
           try {
               remove(tag.getId());
           } catch (PlatformException e) {
               trace.logException("Error remove book tag" , e, TraceLevel.Error);
           }
       }));
    }
}
