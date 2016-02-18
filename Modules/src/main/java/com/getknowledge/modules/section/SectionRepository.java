package com.getknowledge.modules.section;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("SectionRepository")
public class SectionRepository extends BaseRepository<Section> {

    @Override
    protected Class<Section> getClassEntity() {
        return Section.class;
    }

    public Section getSectionByNameAndLanguage(String name , String language) {
        List<Section> section = (List<Section>) entityManager.createQuery("select sec from Section sec where sec.name = :name")
                .setParameter("name" , name).getResultList();
        return section.isEmpty() ? null : section.get(0);
    }
}
