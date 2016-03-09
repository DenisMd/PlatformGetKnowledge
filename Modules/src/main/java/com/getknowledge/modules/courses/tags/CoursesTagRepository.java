package com.getknowledge.modules.courses.tags;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("CoursesTagRepository")
public class CoursesTagRepository extends BaseRepository<CoursesTag> {
    @Override
    protected Class<CoursesTag> getClassEntity() {
        return CoursesTag.class;
    }
}
