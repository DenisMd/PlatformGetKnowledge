package com.getknowledge.modules.programs.group;

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
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import com.getknowledge.platform.modules.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Service("GroupProgramsService")
public class GroupProgramsService extends AbstractService implements ImageService{

    @Autowired
    private GroupProgramsRepository repository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private TraceService trace;

    @Action(name = "getGroupProgramsFromSection" , mandatoryFields = {"sectionId"})
    public List<GroupPrograms> getCourses(HashMap<String,Object> data) {
        long sectionId = new Long((Integer)data.get("sectionId"));

        return repository.getEntitiesByFieldAndValue("section.id" , sectionId);
    }

    @Action(name = "createGroupPrograms" , mandatoryFields = {"sectionId", "title","url"})
    public Result createGroupCourses (HashMap<String,Object> data) throws NotAuthorized {


        long sectionId = Long.parseLong((String) data.get("sectionId"));
        Section section = sectionRepository.read(sectionId);
        if (section == null) {
            return Result.Failed();
        }

        GroupPrograms programs = new GroupPrograms();
        if (!programs.getAuthorizationList().isAccessCreate(userRepository.getSingleEntityByFieldAndValue("login",data.get("principalName")))) {
            throw new NotAuthorized("access denied to create group of programs");
        }
        programs.setTitle((String) data.get("title"));
        programs.setSection(section);
        programs.setUrl((String) data.get("url"));
        repository.create(programs);

        return Result.Complete();
    }

    @ActionWithFile(name = "updateCover" , mandatoryFields = {"id"})
    public Result updateCover (HashMap<String,Object> data, List<MultipartFile> files) throws PlatformException {

        GroupPrograms programs = repository.read(new Long((Integer)data.get("id")));

        if (!isAccessToEdit(data,programs))
            throw new NotAuthorized("access denied");

        try {
            programs.setCover(files.get(0).getBytes());
        } catch (IOException e) {
            trace.logException("Error set cover for group of programs" , e , TraceLevel.Error);
            return Result.Failed();
        }
        repository.update(programs);
        return Result.Complete();
    }

    @Override
    public byte[] getImageById(long id) {
        GroupPrograms programs = repository.read(id);
        if (programs != null)
            return programs.getCover();
        return null;
    }
}
