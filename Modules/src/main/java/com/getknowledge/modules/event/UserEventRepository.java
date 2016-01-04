package com.getknowledge.modules.event;

import com.getknowledge.modules.userInfo.UserInfoRepository;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.exceptions.PlatformException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("UserEventRepository")
public class UserEventRepository extends BaseRepository<UserEvent> {

    @Autowired
    UserInfoRepository userInfoRepository;

    @Override
    protected Class<UserEvent> getClassEntity() {
        return UserEvent.class;
    }



    public void removeWithUser(Long id) throws PlatformException {
        UserEvent userEvent = entityManager.find(getClassEntity(), id);
        long userInfoId = userEvent.getUserInfo().getId();
        super.remove(id);
        userInfoRepository.remove(userInfoId);
    }
}
