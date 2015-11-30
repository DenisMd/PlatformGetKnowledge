package com.getknowledge.modules.settings;

import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

@Service("SettingsService")
public class SettingsService extends AbstractService implements BootstrapService {


    @Qualifier("SettingsRepository")
    @Autowired
    private SettingsRepository settingsRepository;

    @Qualifier("servletContext")
    @Autowired
    private ServletContext servletContext;


    @Override
    public void bootstrap(HashMap<String, Object> map) throws Exception {
        if (settingsRepository.count() == 0) {

            String domainName = "www.getknowledge.com";
            if (map.containsKey("domain")) {
                domainName = (String) map.get("domain");
            }

            Settings settings = new Settings();
            settings.setDomain(domainName);
            Manifest manifest = new Manifest(servletContext.getResourceAsStream("/META-INF/MANIFEST.MF"));
            Attributes mainAttribs = manifest.getMainAttributes();
            String version = mainAttribs.getValue("Implementation-Version");
            settings.setVersion(version);
            settingsRepository.create(settings);
        }
    }

    @Override
    public BootstrapInfo getBootstrapInfo() {
        BootstrapInfo bootstrapInfo = new BootstrapInfo();
        bootstrapInfo.setName("SettingsService");
        bootstrapInfo.setOrder(0);
        bootstrapInfo.setRepeat(false);
        return bootstrapInfo;
    }
}
