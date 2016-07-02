package com.getknowledge.modules.folder;

import com.getknowledge.modules.books.group.GroupBooksRepository;
import com.getknowledge.modules.courses.group.GroupCourses;
import com.getknowledge.modules.courses.group.GroupCoursesRepository;
import com.getknowledge.modules.programs.group.GroupPrograms;
import com.getknowledge.modules.programs.group.GroupProgramsRepository;
import com.getknowledge.modules.section.Section;
import com.getknowledge.modules.section.SectionRepository;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.annotations.ActionWithFile;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.ImageService;
import com.getknowledge.platform.exceptions.NotAuthorized;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.modules.Result;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Service("FolderService")
public class FolderService extends AbstractService implements ImageService {

    @Autowired
    private GroupProgramsRepository groupProgramsRepository;

    @Autowired
    private GroupBooksRepository groupBooksRepository;

    @Autowired
    private GroupCoursesRepository groupCoursesRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private TraceService trace;


    @Action(name = "createFolder" , mandatoryFields = {"sectionId", "title","url","type"})
    @Transactional
    public Result createGroupCourses (HashMap<String,Object> data) throws NotAuthorized {

        long sectionId = longFromField("sectionId",data);
        Section section = sectionRepository.read(sectionId);
        if (section == null) {
            return Result.Failed();
        }

        GroupCourses groupCourses = new GroupCourses();
        if (!groupCourses.getAuthorizationList().isAccessCreate(userRepository.getSingleEntityByFieldAndValue("login",data.get("principalName")))) {
            throw new NotAuthorized("Access denied to create group courses");
        }

        String type = (String) data.get("type");
        switch (type) {
            case "book" :
                groupBooksRepository.createGroupBook((String) data.get("title"), (String) data.get("url"), section);
                break;
            case "program" :
                groupProgramsRepository.createGroupPrograms((String) data.get("title"), (String) data.get("url"), section);
                break;
            case "course" :
                groupCoursesRepository.createGroupCourses((String) data.get("title"),(String) data.get("url"),section);
                break;
        }

        return Result.Complete();
    }

    @ActionWithFile(name = "updateCover" , mandatoryFields = {"id"})
    @Transactional
    public Result updateCover (HashMap<String,Object> data, List<MultipartFile> files) throws PlatformException {

        Folder folder = folderRepository.read(longFromField("id",data));

        if (!isAccessToEdit(data,folder))
            throw new NotAuthorized("access denied");

        try {
            folder.setCover(files.get(0).getBytes());
            folderRepository.merge(folder);
        } catch (IOException e) {
            trace.logException("Error set cover for group of programs" , e , TraceLevel.Error,true);
            return Result.Failed();
        }

        return Result.Complete();
    }

    @Override
    @Transactional
    public byte[] getImageById(long id) {
        Folder folder = folderRepository.read(id);
        return folder == null ? null : folder.getCover();
    }
}
