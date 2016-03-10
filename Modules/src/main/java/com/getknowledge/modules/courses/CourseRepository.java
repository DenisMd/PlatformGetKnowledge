package com.getknowledge.modules.courses;

import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import org.springframework.stereotype.Repository;

@Repository("CourseRepository")
public class CourseRepository extends ProtectedRepository<Course> {
    @Override
    protected Class<Course> getClassEntity() {
        return Course.class;
    }
}
