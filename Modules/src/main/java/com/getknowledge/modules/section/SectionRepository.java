package com.getknowledge.modules.section;

import com.getknowledge.modules.menu.item.MenuItem;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.PrepareRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("SectionRepository")
public class SectionRepository extends BaseRepository<Section> {

    @Override
    protected Class<Section> getClassEntity() {
        return Section.class;
    }

    public Section getSectionByNameAndLanguage(String name , String language) {
        Section section = (Section) entityManager.createQuery("select sec from Section sec where sec.name = :name and sec.language.name = :language")
                .setParameter("name" , name)
                .setParameter("language" , language).getSingleResult();
        return section;
    }
}
