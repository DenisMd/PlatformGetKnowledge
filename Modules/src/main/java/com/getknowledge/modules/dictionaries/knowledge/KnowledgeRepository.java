package com.getknowledge.modules.dictionaries.knowledge;

import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.modules.permission.Permission;
import org.springframework.stereotype.Repository;

@Repository("KnowledgeRepository")
public class KnowledgeRepository extends BaseRepository<Knowledge> {
    @Override
    protected Class<Knowledge> getClassEntity() {
        return Knowledge.class;
    }
}
