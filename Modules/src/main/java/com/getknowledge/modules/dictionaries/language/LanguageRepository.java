package com.getknowledge.modules.dictionaries.language;

import com.getknowledge.modules.dictionaries.language.names.Languages;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.enumerations.RepOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository("LanguageRepository")
public class LanguageRepository extends BaseRepository<Language> {

    @Override
    public List<RepOperations> restrictedOperations() {
        List<RepOperations> operations = new ArrayList<>();
        operations.add(RepOperations.Create);
        operations.add(RepOperations.Remove);
        return operations;
    }

    @Override
    protected Class<Language> getClassEntity() {
        return Language.class;
    }

    public Language getLanguage(Languages languages) {
        return getSingleEntityByFieldAndValue("name", languages.name());
    }

    public void createLanguage(Languages languages) {
        Language language = new Language();
        language.setName(languages.name());
        create(language);
    }
}
