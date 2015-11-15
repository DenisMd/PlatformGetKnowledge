package com.getknowledge.modules.socialLinks;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("SocialLinkRepository")
public class SocialLinkRepository extends BaseRepository<SocialLink> {
    @Override
    protected Class<SocialLink> getClassEntity() {
        return SocialLink.class;
    }
}
