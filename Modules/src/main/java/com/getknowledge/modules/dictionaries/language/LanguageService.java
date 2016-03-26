package com.getknowledge.modules.dictionaries.language;

import com.getknowledge.modules.dictionaries.language.names.Languages;
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
        if (languageRepository.count() == 0) {
            languageRepository.createLanguage(Languages.Ru);
            languageRepository.createLanguage(Languages.En);
        }
    }

    @Override
    public BootstrapInfo getBootstrapInfo() {
        BootstrapInfo bootstrapInfo = new BootstrapInfo();
        bootstrapInfo.setName("Language service");
        bootstrapInfo.setOrder(0);
        return bootstrapInfo;
    }
}
