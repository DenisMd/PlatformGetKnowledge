package com.getknowledge.modules.userInfo;

import com.getknowledge.platform.base.repositories.ProtectedRepository;
import com.getknowledge.platform.modules.user.User;
import com.getknowledge.platform.modules.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("UserInfoRepository")
public class UserInfoRepository extends ProtectedRepository<UserInfo> {

    @Autowired
    private UserRepository userRepository;

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

    @Override
    public void remove(Long id, Class<UserInfo> classEntity) {
        UserInfo userInfo = entityManager.find(classEntity , id);
        long userId = userInfo.getUser().getId();
        super.remove(id, classEntity);
        userRepository.remove(userId , User.class);
    }
}
