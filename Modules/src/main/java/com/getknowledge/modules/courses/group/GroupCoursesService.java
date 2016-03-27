package com.getknowledge.modules.courses.group;

import com.getknowledge.platform.modules.Result;
import com.getknowledge.modules.section.Section;
import com.getknowledge.modules.section.SectionRepository;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.annotations.ActionWithFile;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.ImageService;
import com.getknowledge.platform.exceptions.NotAuthorized;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Service("GroupCoursesService")
public class GroupCoursesService extends AbstractService implements ImageService{

    @Autowired
    private GroupCoursesRepository repository;

    @Autowired
    private SectionRepository sectionRepository;
    
    @Autowired
    private TraceService trace;

    @Action(name = "getGroupCoursesFromSection" , mandatoryFields = {"sectionId"})
    public List<GroupCourses> getCourses(HashMap<String,Object> data) {
        long sectionId = new Long((Integer)data.get("sectionId"));

        return repository.getEntitiesByFieldAndValue("section.id" , sectionId);
    }

    @Action(name = "createGroupCourses" , mandatoryFields = {"sectionId", "title","url"})
    public Result createGroupCourses (HashMap<String,Object> data) throws NotAuthorized {


        long sectionId = Long.parseLong((String) data.get("sectionId"));
        Section section = sectionRepository.read(sectionId);
        if (section == null) {
            return Result.Failed();
        }

        GroupCourses groupCourses = new GroupCourses();
        if (!groupCourses.getAuthorizationList().isAccessCreate(userRepository.getSingleEntityByFieldAndValue("login",data.get("principalName")))) {
            throw new NotAuthorized("access denied to create group courses");
        }
        groupCourses.setTitle((String) data.get("title"));
        groupCourses.setSection(section);
        groupCourses.setUrl((String) data.get("url"));
        repository.create(groupCourses);

        return Result.Complete();
    }

    @ActionWithFile(name = "updateCover" , mandatoryFields = {"id"})
    public Result updateCover (HashMap<String,Object> data, List<MultipartFile> files) throws PlatformException {

        GroupCourses section = repository.read(new Long((Integer)data.get("id")));

        if (!isAccessToEdit(data,section))
            throw new NotAuthorized("access denied");

        try {
            section.setCover(files.get(0).getBytes());
        } catch (IOException e) {
            trace.logException("Error set cover for group section" , e , TraceLevel.Error);
            return Result.Failed();
        }
        repository.update(section);
        return Result.Complete();
    }

    @Override
    public byte[] getImageById(long id) {
        GroupCourses groupCourses = repository.read(id);
        if (groupCourses != null)
            return groupCourses.getCover();
        return null;
    }
}
