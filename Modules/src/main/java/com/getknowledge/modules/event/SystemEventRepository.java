package com.getknowledge.modules.event;

import com.getknowledge.modules.userInfo.UserInfoRepository;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.exceptions.PlatformException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("SystemEventRepository")
public class SystemEventRepository extends BaseRepository<SystemEvent> {

    @Autowired
    UserInfoRepository userInfoRepository;

    @Override
    protected Class<SystemEvent> getClassEntity() {
        return SystemEvent.class;
    }



    public void removeWithUser(Long id) throws PlatformException {
        SystemEvent systemEvent = entityManager.find(getClassEntity(), id);
        long userInfoId = systemEvent.getUserInfo().getId();
        super.remove(id);
        userInfoRepository.remove(userInfoId);
    }
}
