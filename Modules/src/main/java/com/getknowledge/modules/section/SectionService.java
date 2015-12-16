package com.getknowledge.modules.section;

import com.getknowledge.modules.dictionaries.language.Language;
import com.getknowledge.modules.dictionaries.language.LanguageRepository;
import com.getknowledge.modules.menu.Menu;
import com.getknowledge.modules.menu.MenuRepository;
import com.getknowledge.modules.menu.item.MenuItem;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service("SectionService")
@Transactional
public class SectionService extends AbstractService implements BootstrapService{


    @Qualifier("SectionRepository")
    @Autowired
    private SectionRepository sectionRepository;

    @Qualifier("MenuRepository")
    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Override
    public void bootstrap(HashMap<String, Object> map) throws Exception {
        if (sectionRepository.count() == 0) {
            for (Menu menu : menuRepository.list()) {
                for (MenuItem menuItem : menu.getItems()) {
                    if (sectionRepository.getSingleEntityByFieldAndValue("name" , menuItem.getTitle()) == null) {
                        for (Language language : languageRepository.list()) {
                            Section section = new Section();
                            section.setName(menuItem.getTitle());
                            section.setLanguage(language);
                            section.setDescription("empty");
                            section.setMenuItem(menuItem);
                            sectionRepository.create(section);
                        }
                    }
                }
            }
        }
    }

    @Override
    public BootstrapInfo getBootstrapInfo() {
        BootstrapInfo bootstrapInfo = new BootstrapInfo();
        bootstrapInfo.setName("SectionService");
        bootstrapInfo.setOrder(2);
        return bootstrapInfo;
    }

    @Action(name = "getSectionByNameAndLanguage" , mandatoryFields = {"name" , "language"})
    public Section getSectionByNameAndLangugae(HashMap<String, Object> data) {
        return sectionRepository.getSectionByNameAndLanguage((String)data.get("name") , (String)data.get("language"));
    }
}
