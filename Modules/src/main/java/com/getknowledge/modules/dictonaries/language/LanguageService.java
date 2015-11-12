package com.getknowledge.modules.dictonaries.language;

import com.getknowledge.modules.dictonaries.language.names.Languages;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service("LanguageService")
public class LanguageService extends AbstractService implements BootstrapService {

    @Autowired
    private LanguageRepository languageRepository;

    @Override
    public void bootstrap(HashMap<String, Object> map) throws Exception {
        if (languageRepository.count(Language.class) == 0) {
            Language ru = new Language();
            ru.setName(Languages.Ru.name());
            languageRepository.create(ru);

            Language en = new Language();
            en.setName(Languages.En.name());
            languageRepository.create(en);
        }
    }

    @Override
    public BootstrapInfo getBootstrapInfo() {
        BootstrapInfo bootstrapInfo = new BootstrapInfo();
        bootstrapInfo.setName("LanguageService");
        bootstrapInfo.setOrder(0);
        return bootstrapInfo;
    }
}
