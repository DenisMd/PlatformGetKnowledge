package com.getknowledge.modules.dictionaries.programming.languages;

import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

@Service("ProgrammingLanguageService")
public class ProgrammingLanguageService extends AbstractService implements BootstrapService{

    @Autowired
    private ProgrammingLanguageRepository programmingLanguageRepository;

    @Override
    public void bootstrap(HashMap<String, Object> map) throws Exception {
        if (programmingLanguageRepository.count() == 0) {
            InputStream is = getClass().getClassLoader().getResourceAsStream("com.getknowledge.modules/editor/languagesBootstrap");
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    line = line.replaceAll("\\s+", "");
                    String[] split = line.split(":");
                    programmingLanguageRepository.create(split[0],split[1]);
                }
            }
        }
    }

    @Override
    public BootstrapInfo getBootstrapInfo() {
        BootstrapInfo bootstrapInfo = new BootstrapInfo();
        bootstrapInfo.setName("Programming language service");
        bootstrapInfo.setOrder(1);
        return bootstrapInfo;
    }
}
