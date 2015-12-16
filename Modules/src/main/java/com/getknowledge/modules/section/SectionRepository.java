package com.getknowledge.modules.section;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

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
