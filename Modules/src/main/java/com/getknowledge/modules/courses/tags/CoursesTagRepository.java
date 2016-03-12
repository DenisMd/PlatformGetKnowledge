package com.getknowledge.modules.courses.tags;

import com.getknowledge.modules.programs.tags.ProgramTag;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("CoursesTagRepository")
public class CoursesTagRepository extends BaseRepository<CoursesTag> {
    @Override
    protected Class<CoursesTag> getClassEntity() {
        return CoursesTag.class;
    }

    @Autowired
    private TraceService trace;

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
            try {
                remove(tag.getId());
            } catch (PlatformException e) {
                trace.logException("Error remove course tag" , e, TraceLevel.Error);
            }
        }));
    }
}
