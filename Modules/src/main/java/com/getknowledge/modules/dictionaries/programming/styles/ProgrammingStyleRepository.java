package com.getknowledge.modules.dictionaries.programming.styles;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("ProgrammingStylesRepository")
public class ProgrammingStyleRepository extends BaseRepository<ProgrammingStyle> {

    @Override
    protected Class<ProgrammingStyle> getClassEntity() {
        return ProgrammingStyle.class;
    }
}
