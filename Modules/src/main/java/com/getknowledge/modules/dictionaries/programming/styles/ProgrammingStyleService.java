package com.getknowledge.modules.dictionaries.programming.styles;

import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

@Service("ProgrammingStyleService")
public class ProgrammingStyleService extends AbstractService implements BootstrapService {

    @Autowired
    private ProgrammingStyleRepository programmingStyleRepository;

    @Override
    public void bootstrap(HashMap<String, Object> map) throws Exception {
        if (programmingStyleRepository.count() == 0) {
            InputStream is = getClass().getClassLoader().getResourceAsStream("com.getknowledge.modules/editor/stylesBootstrap");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                   programmingStyleRepository.create(line);
                }
            }
        }
    }

    @Override
    public BootstrapInfo getBootstrapInfo() {
        BootstrapInfo bootstrapInfo = new BootstrapInfo();
        bootstrapInfo.setName("Programming style service");
        bootstrapInfo.setOrder(1);
        return bootstrapInfo;
    }
}
