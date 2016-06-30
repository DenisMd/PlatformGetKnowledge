package com.getknowledge.modules.courses.tags;

import com.getknowledge.modules.tags.TagRepository;
import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("CoursesTagRepository")
public class CoursesTagRepository extends TagRepository<CoursesTag> {
    @Override
    protected Class<CoursesTag> getClassEntity() {
        return CoursesTag.class;
    }

    @Override
    protected CoursesTag createTag() {
        return new CoursesTag();
    }

    public void removeUnusedTags() {
        List<CoursesTag> list = entityManager.createQuery("select t from CoursesTag  t where t.courses is empty").getResultList();
        list.forEach((tag -> remove(tag.getId())));
    }
}
