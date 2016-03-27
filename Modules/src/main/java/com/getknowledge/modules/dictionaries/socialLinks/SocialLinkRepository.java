package com.getknowledge.modules.dictionaries.socialLinks;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("SocialLinkRepository")
public class SocialLinkRepository extends BaseRepository<SocialLink> {
    @Override
    protected Class<SocialLink> getClassEntity() {
        return SocialLink.class;
    }

    public void createSocialLink(String name,String link){
        SocialLink socialLink = new SocialLink();
        socialLink.setName(name);
        socialLink.setLink(link);
        create(socialLink);
    }
}
