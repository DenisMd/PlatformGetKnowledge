package com.getknowledge.modules.userInfo;

import com.getknowledge.platform.base.repositories.ProtectedRepository;
import org.springframework.stereotype.Repository;

@Repository("UserInfoRepository")
public class UserInfoRepository extends ProtectedRepository<UserInfo> {

    @Override
    public UserInfo clone(UserInfo entity) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(entity.getId());
        userInfo.setBirthDay(entity.getBirthDay());
        userInfo.setFirstName(entity.getFirstName());
        userInfo.setLanguage(entity.getLanguage());
        userInfo.setLastName(entity.getLastName());
        userInfo.setProfileImage(entity.getProfileImage());
        userInfo.setSpecialty(entity.getSpecialty());
        userInfo.setUser(entity.getUser());
        return userInfo;
    }
}
