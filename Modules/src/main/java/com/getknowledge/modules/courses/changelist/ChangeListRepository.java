package com.getknowledge.modules.courses.changelist;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("ChangeListRepository")
public class ChangeListRepository extends BaseRepository<ChangeList> {
    @Override
    protected Class<ChangeList> getClassEntity() {
        return ChangeList.class;
    }
}
