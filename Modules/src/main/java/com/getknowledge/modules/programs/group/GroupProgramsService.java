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
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Action(name = "createGroupPrograms" , mandatoryFields = {"sectionId", "title","url"})
    @Transactional
    public Result createGroupCourses (HashMap<String,Object> data) throws NotAuthorized {

        long sectionId = longFromField("sectionId",data);
        Section section = sectionRepository.read(sectionId);
        if (section == null) {
            return Result.Failed();
        }

        GroupPrograms programs = new GroupPrograms();
        if (!programs.getAuthorizationList().isAccessCreate(userRepository.getSingleEntityByFieldAndValue("login",data.get("principalName")))) {
            throw new NotAuthorized("access denied to create group of programs");
        }

        repository.createGroupPrograms((String)data.get("title"),(String)data.get("url"),section);

        return Result.Complete();
    }

    @ActionWithFile(name = "updateCover" , mandatoryFields = {"id"})
    @Transactional
    public Result updateCover (HashMap<String,Object> data, List<MultipartFile> files) throws PlatformException {

        GroupPrograms programs = repository.read(longFromField("id",data));

        if (!isAccessToEdit(data,programs))
            throw new NotAuthorized("access denied");

        try {
            programs.setCover(files.get(0).getBytes());
            repository.merge(programs);
        } catch (IOException e) {
            trace.logException("Error set cover for group of programs" , e , TraceLevel.Error);
            return Result.Failed();
        }

        return Result.Complete();
    }

    @Override
    public byte[] getImageById(long id) {
        GroupPrograms programs = repository.read(id);
        return programs == null ? null : programs.getCover();
    }
}
