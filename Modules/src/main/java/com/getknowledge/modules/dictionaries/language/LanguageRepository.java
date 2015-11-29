package com.getknowledge.modules.dictionaries.language;

import com.getknowledge.modules.dictionaries.language.names.Languages;
import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("LanguageRepository")
public class LanguageRepository extends BaseRepository<Language> {
    @Override
    protected Class<Language> getClassEntity() {
        return Language.class;
    }

    public Language getLanguage(Languages languages) {
        return getSingleEntityByFieldAndValue("name", languages.name());
    }
}
