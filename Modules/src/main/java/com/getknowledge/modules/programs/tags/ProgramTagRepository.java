package com.getknowledge.modules.programs.tags;

import com.getknowledge.modules.tags.TagRepository;
import org.springframework.stereotype.Repository;

@Repository("ProgramTagRepository")
public class ProgramTagRepository extends TagRepository<ProgramTag>{

    @Override
    protected Class<ProgramTag> getClassEntity() {
        return ProgramTag.class;
    }


}
