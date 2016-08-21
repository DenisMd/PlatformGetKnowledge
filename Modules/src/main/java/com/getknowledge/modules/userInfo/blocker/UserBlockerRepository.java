package com.getknowledge.modules.userInfo.blocker;

import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("UserBlockerRepository")
public class UserBlockerRepository extends BaseRepository<UserBlocker> {
    @Override
    protected Class<UserBlocker> getClassEntity() {
        return UserBlocker.class;
    }

    public UserBlocker getBlockerByTypeAndUser (UserInfo userInfo,BlockerTypes type) {
        if (userInfo == null || type == null) {
            return null;
        }
        List<UserBlocker> blockerList = entityManager.createQuery("select ub from UserBlocker  ub where ub.userInfo.id = :userId and ub.blockerTypes = :blockerType", UserBlocker.class)
                .setParameter("userId", userInfo.getId())
                .setParameter("blockerType", type)
                .getResultList();
        return blockerList.isEmpty() ? null : blockerList.get(0);
    }
}
