package com.getknowledge.modules.socialLinks;

import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
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

            SocialLink vk = new SocialLink();
            vk.setName("vk");

            SocialLink facebook = new SocialLink();
            facebook.setName("facebook");

            SocialLink twitter = new SocialLink();
            twitter.setName("twitter");

            SocialLink gitHub = new SocialLink();
            gitHub.setName("gitHub");

            socialLinkRepository.create(vk);
            socialLinkRepository.create(facebook);
            socialLinkRepository.create(twitter);
            socialLinkRepository.create(gitHub);
        }
    }

    @Override
    public BootstrapInfo getBootstrapInfo() {
        BootstrapInfo bootstrapInfo = new BootstrapInfo();
        bootstrapInfo.setName("SocialLinksService");
        bootstrapInfo.setOrder(1);
        bootstrapInfo.setRepeat(false);
        return bootstrapInfo;
    }
}
