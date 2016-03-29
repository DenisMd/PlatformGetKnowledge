package com.getknowledge.modules.courses.changelist;

import com.getknowledge.modules.courses.Course;
import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository("ChangeListRepository")
public class ChangeListRepository extends BaseRepository<ChangeList> {
    @Override
    protected Class<ChangeList> getClassEntity() {
        return ChangeList.class;
    }

    public ChangeList createChangeList(Course course,String text){
        return createChangeList(course, Arrays.asList(text));
    }

    public ChangeList createChangeList(Course course,List<String> changes){
        ChangeList changeList = new ChangeList();
        changeList.setCourse(course);
        changeList.setVersion(course.getVersion());
        changeList.setChangeList(changes);
        create(changeList);
        return changeList;
    }
}
