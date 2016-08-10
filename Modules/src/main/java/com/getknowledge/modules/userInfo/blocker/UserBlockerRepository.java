package com.getknowledge.modules.userInfo.blocker;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("UserBlockerRepository")
public class UserBlockerRepository extends BaseRepository<UserBlocker> {
    @Override
    protected Class<UserBlocker> getClassEntity() {
        return UserBlocker.class;
    }
}
