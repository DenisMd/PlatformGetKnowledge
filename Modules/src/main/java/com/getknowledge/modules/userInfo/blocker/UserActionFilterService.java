package com.getknowledge.modules.userInfo.blocker;

import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoRepository;
import com.getknowledge.modules.userInfo.blocker.info.BlockerInfo;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service("UserActionFilterService")
public class UserActionFilterService {

    @Autowired
    private UserBlockerRepository userBlockerRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    //После 15 мин, мы обновляем информацию о пользователе
    private int updateTimeInterval = 15;

    //UserInfoId,Info
    private Map<Long,BlockerInfo> activeInfo = new ConcurrentHashMap<>();

    @Transactional
    public boolean filterAction(UserInfo userInfo,String ip,BlockerTypes blockerTypes) {
        //1 Проверка заблокирован ли пользователь

        if (!checkBlockers(userInfo,blockerTypes)) {
            return false;
        }

        //2 Добавление информации о сообщении

        return true;
    }


    private boolean checkBlockers(UserInfo userInfo,BlockerTypes blockerTypes) {
        return userBlockerRepository.getBlockerByTypeAndUser(userInfo,blockerTypes) == null;
    }

    private void addInfo(UserInfo userInfo , String ip, BlockerTypes type) {
        BlockerInfo blockerInfo = null;
        if (activeInfo.containsKey(userInfo.getId())) {
            blockerInfo = activeInfo.get(userInfo.getId());
        }
    }
}
