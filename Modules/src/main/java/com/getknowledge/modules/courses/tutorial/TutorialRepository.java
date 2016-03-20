package com.getknowledge.modules.courses.tutorial;

import com.getknowledge.modules.courses.Course;
import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Calendar;

@Repository("TutorialRepository")
public class TutorialRepository extends BaseRepository<Tutorial> {
    @Override
    protected Class<Tutorial> getClassEntity() {
        return Tutorial.class;
    }

    @Override
    public void create(Tutorial object) {
        super.create(object);
    }
}
