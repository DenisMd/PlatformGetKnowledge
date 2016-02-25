package com.getknowledge.modules.dictionaries.programming.styles;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("ProgrammingStylesRepository")
public class ProgrammingStylesRepository extends BaseRepository<ProgrammingStyles> {

    @Override
    protected Class<ProgrammingStyles> getClassEntity() {
        return ProgrammingStyles.class;
    }
}
