package com.getknowledge.modules.event.user;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("UserEventRepository")
public class UserEventRepository extends BaseRepository<UserEvent> {
    @Override
    protected Class<UserEvent> getClassEntity() {
        return UserEvent.class;
    }
}
