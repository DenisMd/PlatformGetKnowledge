package com.getknowledge.modules.courses.group;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("GroupCoursesRepository")
public class GroupCoursesRepository extends BaseRepository<GroupCourses> {
    @Override
    protected Class<GroupCourses> getClassEntity() {
        return GroupCourses.class;
    }
}
