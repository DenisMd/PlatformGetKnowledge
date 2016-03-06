package com.getknowledge.modules.programs;

import com.getknowledge.modules.books.Book;
import com.getknowledge.modules.books.tags.BooksTag;
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
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    private ProgramTagRepository programTagRepository;

    @Autowired
    private GroupProgramsRepository groupProgramsRepository;

    @Autowired
    private TraceService trace;

    @Autowired
    private ProgramRepository programRepository;

    private void prepareTag(HashMap<String,Object> data , Program program) {
        if (data.containsKey("tags")) {
            List<String> tags = (List<String>) data.get("tags");
            for (String tag : tags) {
                ProgramTag programTag = programTagRepository.createIfNotExist(tag);
                program.getTags().add(programTag);
            }
        }
    }


    private void removeTagsFromProgram(Program program) {

        for (ProgramTag programTag : program.getTags()) {
            programTag.getPrograms().remove(program);
            programTagRepository.merge(programTag);
        }

        program.getTags().clear();
    }

    private void prepareLinks(HashMap<String,Object> data , Program program) {
        if (data.containsKey("links")) {
            List<String> list = (List<String>) data.get("links");
            program.setLinks(list);
        }
    }

    private Result checkProgramRight(HashMap<String,Object> data) {
        Long programId = new Long((Integer)data.get("programId"));
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
    public Result createProgram(HashMap<String,Object> data) {
        if (!data.containsKey("principalName"))
            return Result.NotAuthorized();

        UserInfo userInfo = userInfoService.getAuthorizedUser(data);

        Program program = new Program();
        program.setOwner(userInfo);

        if (!program.getAuthorizationList().isAccessCreate(userInfo.getUser())) {
            return Result.AccessDenied();
        }

        Long groupBookId = new Long((Integer)data.get("groupBookId"));

        GroupPrograms groupPrograms =  groupProgramsRepository.read(groupBookId);
        if (groupPrograms == null) {
            trace.log("Group program id is incorrect" , TraceLevel.Warning);
            return Result.Failed();
        }

        program.setGroupPrograms(groupPrograms);

        program.setName((String) data.get("name"));
        program.setDescription((String) data.get("description"));

        try {
            Language language = languageRepository.getLanguage(Languages.valueOf((String) data.get("language")));
            program.setLanguage(language);
        } catch (Exception exception) {
            Result result = Result.Failed();
            result.setObject("Language not found");
            return result;
        }

        prepareLinks(data,program);

        prepareTag(data,program);

        programRepository.create(program);

        for (ProgramTag programTag : program.getTags()) {
            programTag.getPrograms().add(program);
            programTagRepository.merge(programTag);
        }
        Result result = Result.Complete();
        result.setObject(program.getId());
        return result;
    }

    @Action(name = "updateProgramInformation" , mandatoryFields = {"programId"})
    public Result updateProgramInformation(HashMap<String,Object> data) {

        Result result = checkProgramRight(data);
        Program program;
        if (result.getObject() != null)  {
            program = (Program) result.getObject();
        } else {
            return result;
        }


        if (data.containsKey("name")) {
            String name = (String) data.get("name");
            program.setName(name);
        }

        if (data.containsKey("description")) {
            String description = (String) data.get("description");
            program.setDescription(description);
        }

        prepareLinks(data,program);

        removeTagsFromProgram(program);
        prepareTag(data,program);

        programRepository.merge(program);
        for (ProgramTag programTag : program.getTags()) {
            programTag.getPrograms().add(program);
            programTagRepository.merge(programTag);
        }

        programTagRepository.removeUnusedTags();

        return Result.Complete();
    }

    @ActionWithFile(name = "uploadCover" , mandatoryFields = {"programId"})
    public Result updataCover(HashMap<String,Object> data, List<MultipartFile> files) {
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
            trace.logException("Error read cover for program" , e, TraceLevel.Warning);
            return Result.Failed();
        }

        programRepository.merge(program);
        return Result.Complete();
    }

    @ActionWithFile(name = "uploadData" , mandatoryFields = {"programId"})
    public Result updataData(HashMap<String,Object> data, List<MultipartFile> files) {
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
            trace.logException("Error read data for program" , e, TraceLevel.Warning);
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
