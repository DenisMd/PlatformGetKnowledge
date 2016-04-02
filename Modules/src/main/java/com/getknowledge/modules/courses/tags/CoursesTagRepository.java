package com.getknowledge.modules.courses.tags;

import com.getknowledge.platform.base.entities.EntityWithTags;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.ITagRepository;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("CoursesTagRepository")
public class CoursesTagRepository extends BaseRepository<CoursesTag> implements ITagRepository<CoursesTag> {
    @Override
    protected Class<CoursesTag> getClassEntity() {
        return CoursesTag.class;
    }

    @Autowired
    private TraceService trace;

    @Override
    public void removeTagsFromEntity(EntityWithTags<CoursesTag> entity) {
        for (CoursesTag coursesTag : entity.getTags()) {
            coursesTag.getCourses().remove(entity);
            merge(coursesTag);
        }

        entity.getTags().clear();
    }

    @Override
    public void createTags(List<String> tags, EntityWithTags<CoursesTag> entity) {
        for (String tag : tags) {
            CoursesTag coursesTag = createIfNotExist(tag);
            entity.getTags().add(coursesTag);
        }
    }

    public CoursesTag createIfNotExist(String tag) {
        CoursesTag result = getSingleEntityByFieldAndValue("tagName" , tag);

        if (result == null) {
            result = new CoursesTag();
            result.setTagName(tag);
            create(result);
        }

        return result;
    }

    public void removeUnusedTags() {
        List<CoursesTag> list = entityManager.createQuery("select t from CoursesTag  t where t.courses is empty").getResultList();
        list.forEach((tag -> {
            remove(tag.getId());
        }));
    }
}
