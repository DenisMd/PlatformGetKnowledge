package com.getknowledge.modules.userInfo.restore.password;

import com.getknowledge.modules.userInfo.UserInfoRepository;
import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository("RestorePasswordInfoRepository")
public class RestorePasswordInfoRepository extends BaseRepository<RestorePasswordInfo> {

    @Override
    protected Class<RestorePasswordInfo> getClassEntity() {
        return RestorePasswordInfo.class;
    }

    @Autowired
    UserInfoRepository userInfoRepository;
}
