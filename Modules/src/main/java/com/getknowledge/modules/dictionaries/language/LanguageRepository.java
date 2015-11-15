package com.getknowledge.modules.dictionaries.language;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("LanguageRepository")
public class LanguageRepository extends BaseRepository<Language> {
    @Override
    protected Class<Language> getClassEntity() {
        return Language.class;
    }
}
