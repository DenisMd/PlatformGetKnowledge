package com.getknowledge.modules.programs.group;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("GroupProgramsRepository")
public class GroupProgramsRepository extends BaseRepository<GroupPrograms> {
    @Override
    protected Class<GroupPrograms> getClassEntity() {
        return GroupPrograms.class;
    }
}
