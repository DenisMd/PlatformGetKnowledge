package com.getknowledge.modules.event.user;

import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Calendar;

@Repository("UserEventRepository")
public class UserEventRepository extends BaseRepository<UserEvent> {
    @Override
    protected Class<UserEvent> getClassEntity() {
        return UserEvent.class;
    }


    public UserEvent createUserEvent(UserInfo owner,String data, UserEventType type){
        UserEvent userEvent = new UserEvent();
        userEvent.setCreateTime(Calendar.getInstance());
        userEvent.setOwner(owner);
        userEvent.setUserEventType(type);
        userEvent.setData(data);
        userEvent.setChecked(false);
        create(userEvent);
        return userEvent;
    }
}
