package com.getknowledge.modules.courses;

import com.getknowledge.platform.base.repositories.BaseRepository;

public class CourseRepository extends BaseRepository<Course> {
    @Override
    protected Class<Course> getClassEntity() {
        return Course.class;
    }
}
