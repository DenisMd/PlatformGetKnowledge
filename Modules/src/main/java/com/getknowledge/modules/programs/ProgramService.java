package com.getknowledge.modules.programs;

import com.getknowledge.modules.dictionaries.language.Language;
import com.getknowledge.modules.dictionaries.language.LanguageRepository;
import com.getknowledge.modules.dictionaries.language.names.Languages;
import com.getknowledge.modules.programs.group.GroupPrograms;
import com.getknowledge.modules.programs.group.GroupProgramsRepository;
import com.getknowledge.modules.programs.tags.ProgramTag;
import com.getknowledge.modules.programs.tags.ProgramTagRepository;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoService;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.annotations.ActionWithFile;
import com.getknowledge.platform.base.serializers.FileResponse;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.FileService;
import com.getknowledge.platform.base.services.ImageService;
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

@Service("ProgramService")
public class ProgramService extends AbstractService  implements ImageService,FileService {
    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private GroupProgramsRepository groupProgramsRepository;

    @Autowired
    private TraceService trace;

    @Autowired
    private ProgramRepository programRepository;

    private Result checkProgramRight(HashMap<String,Object> data) {
        Long programId = new Long(longFromField("programId",data));
        Program program = programRepository.read(programId);
        if (program == null) {
            return Result.NotFound();
        }

        UserInfo userInfo = userInfoService.getAuthorizedUser(data);

        if (!program.getAuthorizationList().isAccessEdit(userInfo.getUser())) {
            return Result.AccessDenied();
        }

        Result result = Result.Complete();
        result.setObject(program);
        return result;
    }


    @Action(name = "createProgram" , mandatoryFields = {"name","groupProgramId","description","language"})
    @Transactional
    public Result createProgram(HashMap<String,Object> data) {
        if (!data.containsKey("principalName"))
            return Result.NotAuthorized();

        UserInfo userInfo = userInfoService.getAuthorizedUser(data);

        Program program = new Program();
        program.setOwner(userInfo);

        if (!program.getAuthorizationList().isAccessCreate(userInfo.getUser())) {
            return Result.AccessDenied();
        }

        Long groupBookId = new Long((Integer)data.get("groupProgramId"));

        GroupPrograms groupPrograms =  groupProgramsRepository.read(groupBookId);
        if (groupPrograms == null) {
            trace.log("Group program is not found" , TraceLevel.Warning,false);
            return Result.Failed();
        }

        Language language = null;

        try {
            language = languageRepository.getLanguage(Languages.valueOf((String) data.get("language")));
            program.setLanguage(language);
        } catch (Exception exception) {
            Result result = Result.Failed();
            result.setObject("Language not found");
            return result;
        }

        String name = (String) data.get("name");
        String description = (String) data.get("description");
        List<String> links = null;
        List<String> tags = null;

        if (data.containsKey("links")){
            links = (List<String>) data.get("links");
        }

        if (data.containsKey("tags")){
            links = (List<String>) data.get("tags");
        }


        programRepository.createProgram(groupPrograms,userInfo,name,description,language,links,tags);

        Result result = Result.Complete();
        result.setObject(program.getId());
        return result;
    }

    @Action(name = "updateProgramInformation" , mandatoryFields = {"programId"})
    @Transactional
    public Result updateProgramInformation(HashMap<String,Object> data) {

        Result result = checkProgramRight(data);
        Program program;
        if (result.getObject() != null)  {
            program = (Program) result.getObject();
        } else {
            return result;
        }

        String name = (String) data.get("name");
        String description = (String) data.get("description");
        List<String> links = null;
        List<String> tags = null;

        if (data.containsKey("links")){
            links = (List<String>) data.get("links");
        }

        if (data.containsKey("tags")){
            links = (List<String>) data.get("tags");
        }

        programRepository.updateProgram(program, name, description, links, tags);

        return Result.Complete();
    }

    @ActionWithFile(name = "uploadCover" , mandatoryFields = {"programId"})
    @Transactional
    public Result uploadCover(HashMap<String,Object> data, List<MultipartFile> files) {
        Result result = checkProgramRight(data);
        Program program;
        if (result.getObject() != null)  {
            program = (Program) result.getObject();
        } else {
            return result;
        }

        try {
            program.setCover(files.get(0).getBytes());
        } catch (IOException e) {
            trace.logException("Error upload vocer for program" , e, TraceLevel.Warning,true);
            return Result.Failed();
        }

        programRepository.merge(program);
        return Result.Complete();
    }

    @ActionWithFile(name = "uploadData" , mandatoryFields = {"programId"})
    @Transactional
    public Result uploadData(HashMap<String,Object> data, List<MultipartFile> files) {
        Result result = checkProgramRight(data);
        Program program;
        if (result.getObject() != null)  {
            program = (Program) result.getObject();
        } else {
            return result;
        }

        try {
            program.setBookData(files.get(0).getBytes());
            program.setFileName(files.get(0).getOriginalFilename());
        } catch (IOException e) {
            trace.logException("Error upload data for program" , e, TraceLevel.Warning,true);
            return Result.Failed();
        }

        programRepository.merge(program);
        return Result.Complete();
    }

    @Override
    public byte[] getImageById(long id) {
        Program program = programRepository.read(id);
        return program == null ? null : program.getCover();
    }

    @Override
    public FileResponse getFile(long id, Object key) {
        FileResponse fileResponse = new FileResponse();
        Program program = programRepository.read(id);
        if (program != null)  {
            fileResponse.setData(program.getBookData());
            fileResponse.setFileName(program.getFileName());
        }
        return fileResponse;
    }
}
