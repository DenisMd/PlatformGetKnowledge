package com.getknowledge.modules.dictionaries.socialLinks;

import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.enumerations.RepOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository("SocialLinkRepository")
public class SocialLinkRepository extends BaseRepository<SocialLink> {
    @Override
    public List<RepOperations> restrictedOperations() {
        List<RepOperations> operations = new ArrayList<>();
        operations.add(RepOperations.Create);
        operations.add(RepOperations.Remove);
        return operations;
    }


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
