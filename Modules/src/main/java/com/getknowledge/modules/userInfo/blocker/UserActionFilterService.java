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

    //Ip,Info
    private Map<String,BlockerInfo> activeInfo = new ConcurrentHashMap<>();

    @Transactional
    public boolean filterAction(UserInfo userInfo,String ip,BlockerTypes blockerTypes) {

        return true;
    }
}
