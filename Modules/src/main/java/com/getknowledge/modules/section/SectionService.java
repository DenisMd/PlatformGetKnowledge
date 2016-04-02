package com.getknowledge.modules.section;

import com.getknowledge.modules.menu.Menu;
import com.getknowledge.modules.menu.enumerations.MenuNames;
import com.getknowledge.modules.menu.MenuRepository;
import com.getknowledge.modules.menu.item.MenuItem;
import com.getknowledge.platform.modules.Result;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.annotations.ActionWithFile;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.base.services.ImageService;
import com.getknowledge.platform.exceptions.NotAuthorized;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Service("SectionService")
@Transactional
public class SectionService extends AbstractService implements BootstrapService, ImageService{


    @Qualifier("SectionRepository")
    @Autowired
    private SectionRepository sectionRepository;

    @Qualifier("MenuRepository")
    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private TraceService trace;

    @Override
    public void bootstrap(HashMap<String, Object> map) throws Exception {
        if (sectionRepository.count() == 0) {
            for (Menu menu : menuRepository.list()) {
                if (menu.getName().equals(MenuNames.AuthorizedUser.name()) || menu.getName().equals(MenuNames.NotAuthorizedUser.name()))
                    continue;
                for (MenuItem menuItem : menu.getItems()) {
                    if (sectionRepository.getSingleEntityByFieldAndValue("name" , menuItem.getTitle()) == null) {
                        Section section = new Section();
                        section.setName(menuItem.getTitle());
                        section.setMenuItem(menuItem);
                        sectionRepository.create(section);
                    }
                }
            }
        }
    }

    @Override
    public BootstrapInfo getBootstrapInfo() {
        BootstrapInfo bootstrapInfo = new BootstrapInfo();
        bootstrapInfo.setName("Section service");
        bootstrapInfo.setOrder(2);
        return bootstrapInfo;
    }

    @Action(name = "getSectionByNameAndLanguage" , mandatoryFields = {"name" , "language"})
    @Transactional
    public Section getSectionByNameAndLangugae(HashMap<String, Object> data) {
        return sectionRepository.getSectionByNameAndLanguage((String)data.get("name") , (String)data.get("language"));
    }


    @Override
    @Transactional
    public byte[] getImageById(long id) {
        Section section = sectionRepository.read(id);
        byte [] cover = section.getCover();
        return cover;
    }

    @ActionWithFile(name = "updateCover" , mandatoryFields = {"id"})
    @Transactional
    public Result updateCover (HashMap<String,Object> data, List<MultipartFile> files) throws PlatformException {
        Section section = sectionRepository.read(longFromField("id",data));

        if (!isAccessToEdit(data,section))
            throw new NotAuthorized("Access denied");

        try {
            section.setCover(files.get(0).getBytes());
            sectionRepository.merge(section);
        } catch (IOException e) {
            trace.logException("Error set cover for section",e,TraceLevel.Error);
            return Result.Failed();
        }

        return Result.Complete();
    }
}
