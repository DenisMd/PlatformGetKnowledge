package com.getknowledge.modules.userInfo.registerInfo;

import com.getknowledge.modules.userInfo.UserInfoRepository;
import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository("RegisterInfoRepository")
public class RegisterInfoRepository extends BaseRepository<RegisterInfo> {

    @Override
    protected Class<RegisterInfo> getClassEntity() {
        return RegisterInfo.class;
    }

    @Autowired
    UserInfoRepository userInfoRepository;

    @Override
    public void remove(Long id) {
        RegisterInfo registerInfo = entityManager.find(getClassEntity(), id);
        long userInfoId = registerInfo.getUserInfo().getId();
        super.remove(id);
        userInfoRepository.remove(userInfoId);
    }
}
