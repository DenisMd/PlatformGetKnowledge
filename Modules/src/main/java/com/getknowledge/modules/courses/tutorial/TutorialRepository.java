package com.getknowledge.modules.courses.tutorial;

import com.getknowledge.modules.courses.Course;
import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("TutorialRepository")
public class TutorialRepository extends BaseRepository<Tutorial> {
    @Override
    protected Class<Tutorial> getClassEntity() {
        return Tutorial.class;
    }
}
