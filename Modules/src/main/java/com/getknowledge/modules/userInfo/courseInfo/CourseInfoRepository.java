package com.getknowledge.modules.userInfo.courseInfo;

import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("CourseInfoRepository")
public class CourseInfoRepository extends BaseRepository<CourseInfo> {
    @Override
    protected Class<CourseInfo> getClassEntity() {
        return CourseInfo.class;
    }
}
