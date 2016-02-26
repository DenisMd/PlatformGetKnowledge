package com.getknowledge.modules.dictionaries.programming.languages;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("ProgrammingLanguageRepository")
public class ProgrammingLanguageRepository extends BaseRepository<ProgrammingLanguage> {

    @Override
    protected Class<ProgrammingLanguage> getClassEntity() {
        return ProgrammingLanguage.class;
    }
}
