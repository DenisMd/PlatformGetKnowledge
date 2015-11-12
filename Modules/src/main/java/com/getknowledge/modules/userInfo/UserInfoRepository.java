package com.getknowledge.modules.userInfo;

import com.getknowledge.modules.dictonaries.language.Language;
import com.getknowledge.modules.dictonaries.language.LanguageRepository;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import com.getknowledge.platform.modules.user.User;
import com.getknowledge.platform.modules.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("UserInfoRepository")
public class UserInfoRepository extends ProtectedRepository<UserInfo> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LanguageRepository languageRepository;

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
        long languageId = userInfo.getLanguage().getId();
        super.remove(id, classEntity);
        languageRepository.remove(languageId,Language.class);
        userRepository.remove(userId , User.class);
    }
}
