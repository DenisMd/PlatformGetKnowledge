package com.getknowledge.modules.userInfo.registerInfo;

import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoRepository;
import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository("RegisterInfoRepository")
public class RegisterInfoRepository extends BaseRepository<RegisterInfo> {

    @Autowired
    UserInfoRepository userInfoRepository;

    @Override
    public void remove(Long id, Class<RegisterInfo> classEntity) {
        RegisterInfo registerInfo = entityManager.find(classEntity, id);
        long userInfoId = registerInfo.getUserInfo().getId();
        super.remove(id , classEntity);
        userInfoRepository.remove(userInfoId , UserInfo.class);
    }
}
