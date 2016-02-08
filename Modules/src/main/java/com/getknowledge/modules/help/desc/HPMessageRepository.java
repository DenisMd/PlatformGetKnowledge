package com.getknowledge.modules.help.desc;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("HPMessageRepository")
public class HPMessageRepository extends BaseRepository<HpMessage> {
    @Override
    protected Class<HpMessage> getClassEntity() {
        return HpMessage.class;
    }
}
