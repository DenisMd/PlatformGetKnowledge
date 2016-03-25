package com.getknowledge.modules.dictionaries.socialLinks;

import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import org.ini4j.Ini;
import org.ini4j.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service("SocialLinksService")
public class SocialLinksService extends AbstractService implements BootstrapService {

    @Autowired
    private SocialLinkRepository socialLinkRepository;

    @Override
    public void bootstrap(HashMap<String, Object> map) throws Exception {
        if (socialLinkRepository.count() == 0) {
            Ini ini = new Ini(getClass().getClassLoader().getResourceAsStream("com.getknowledge.modules/socialLinks/socialLinkBootstrap"));
            for (String name : ini.keySet()) {
                Profile.Section section = ini.get(name);
                String link = section.get("link",String.class);
                socialLinkRepository.createSocialLink(name,link);
            }
        }
    }

    @Override
    public BootstrapInfo getBootstrapInfo() {
        BootstrapInfo bootstrapInfo = new BootstrapInfo();
        bootstrapInfo.setName("Social links service");
        bootstrapInfo.setOrder(1);
        bootstrapInfo.setRepeat(false);
        return bootstrapInfo;
    }
}
