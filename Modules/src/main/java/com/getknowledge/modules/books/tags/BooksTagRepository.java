package com.getknowledge.modules.books.tags;

import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("BooksTagRepository")
public class BooksTagRepository extends BaseRepository<BooksTag> {

    @Autowired
    private TraceService trace;

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
