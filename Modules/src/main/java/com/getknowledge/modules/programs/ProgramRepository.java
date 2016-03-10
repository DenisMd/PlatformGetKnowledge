package com.getknowledge.modules.programs;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("ProgramRepository")
public class ProgramRepository extends BaseRepository<Program> {

    @Override
    protected Class<Program> getClassEntity() {
        return Program.class;
    }


}
