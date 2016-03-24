package com.getknowledge.modules.books.group;

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

@Service("GroupBooksService")
public class GroupBooksService extends AbstractService implements ImageService{

    @Autowired
    private GroupBooksRepository repository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private TraceService trace;

    @Action(name = "getGroupBooksFromSection" , mandatoryFields = {"sectionId"})
    public List<GroupBooks> getCourses(HashMap<String,Object> data) {
        long sectionId = new Long((Integer)data.get("sectionId"));

        return repository.getEntitiesByFieldAndValue("section.id" , sectionId);
    }

    @Action(name = "createGroupBooks" , mandatoryFields = {"sectionId", "title","url"})
    public Result createGroupCourses (HashMap<String,Object> data) throws NotAuthorized {


        long sectionId = Long.parseLong((String) data.get("sectionId"));
        Section section = sectionRepository.read(sectionId);
        if (section == null) {
            return Result.Failed();
        }

        GroupBooks groupBooks = new GroupBooks();
        if (!groupBooks.getAuthorizationList().isAccessCreate(userRepository.getSingleEntityByFieldAndValue("login",data.get("principalName")))) {
            throw new NotAuthorized("access denied to create group books");
        }
        groupBooks.setTitle((String) data.get("title"));
        groupBooks.setSection(section);
        groupBooks.setUrl((String) data.get("url"));
        repository.create(groupBooks);

        return Result.Complete();
    }

    @ActionWithFile(name = "updateCover" , mandatoryFields = {"id"})
    public Result updateCover (HashMap<String,Object> data, List<MultipartFile> files) throws PlatformException {

        GroupBooks books = repository.read(new Long((Integer)data.get("id")));

        if (!isAccessToEdit(data,books))
            throw new NotAuthorized("access denied");

        try {
            books.setCover(files.get(0).getBytes());
        } catch (IOException e) {
            trace.logException("Error set cover for group book" , e , TraceLevel.Error);
            return Result.Failed();
        }
        repository.update(books);
        return Result.Complete();
    }

    @Override
    public byte[] getImageById(long id) {
        GroupBooks books = repository.read(id);
        if (books != null)
            return books.getCover();
        return null;
    }
}
