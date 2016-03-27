package com.getknowledge.modules.event;

import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoRepository;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.exceptions.PlatformException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Calendar;

@Repository("SystemEventRepository")
public class SystemEventRepository extends BaseRepository<SystemEvent> {

    @Autowired
    UserInfoRepository userInfoRepository;

    @Override
    protected Class<SystemEvent> getClassEntity() {
        return SystemEvent.class;
    }

    public SystemEvent createSystemEvent(UserInfo userInfo,String uuid,SystemEventType systemEventType) {
        SystemEvent registerInfo = new SystemEvent();
        registerInfo.setUserInfo(userInfo);
        registerInfo.setCalendar(Calendar.getInstance());
        registerInfo.setUuid(uuid);
        registerInfo.setSystemEventType(systemEventType);
        create(registerInfo);
        return registerInfo;
    }

    public void removeWithUser(Long id) throws PlatformException {
        SystemEvent systemEvent = entityManager.find(getClassEntity(), id);
        long userInfoId = systemEvent.getUserInfo().getId();
        super.remove(id);
        userInfoRepository.remove(userInfoId);
    }
}
