package com.getknowledge.modules.section;

import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import com.getknowledge.platform.base.repositories.enumerations.RepOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository("SectionRepository")
public class SectionRepository extends ProtectedRepository<Section> {

    @Override
    public List<RepOperations> restrictedOperations() {
        List<RepOperations> operations = new ArrayList<>();
        operations.add(RepOperations.Create);
        operations.add(RepOperations.Remove);
        return operations;
    }

    @Override
    protected Class<Section> getClassEntity() {
        return Section.class;
    }

    public Section getSectionByNameAndLanguage(String name) {
        List<Section> section = (List<Section>) entityManager.createQuery("select sec from Section sec where sec.name = :name")
                .setParameter("name" , name).getResultList();
        return section.isEmpty() ? null : section.get(0);
    }
}
