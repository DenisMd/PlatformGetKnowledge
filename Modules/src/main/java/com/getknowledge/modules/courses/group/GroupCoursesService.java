package com.getknowledge.modules.courses.group;

import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.services.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service("GroupCoursesService")
public class GroupCoursesService extends AbstractService {

    @Autowired
    private GroupCoursesRepository repository;

    @Action(name = "getGroupCoursesFromSection" , mandatoryFields = {"sectionId"})
    public List<GroupCourses> getCourses(HashMap<String,Object> data) {
        long sectionId = new Long((Integer)data.get("sectionId"));

        return repository.getEntitiesByFieldAndValue("section.id" , sectionId);
    }

}
