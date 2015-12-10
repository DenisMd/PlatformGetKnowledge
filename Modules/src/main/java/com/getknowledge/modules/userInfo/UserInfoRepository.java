package com.getknowledge.modules.userInfo;

import com.getknowledge.modules.dictionaries.language.Language;
import com.getknowledge.modules.dictionaries.language.LanguageRepository;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import com.getknowledge.platform.modules.user.User;
import com.getknowledge.platform.modules.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("UserInfoRepository")
public class UserInfoRepository extends ProtectedRepository<UserInfo> {

    @Override
    protected Class<UserInfo> getClassEntity() {
        return UserInfo.class;
    }

    @Autowired
    private UserRepository userRepository;


    @Override
    public void remove(Long id) {
        UserInfo userInfo = entityManager.find(getClassEntity() , id);
        long userId = userInfo.getUser().getId();
        super.remove(id);
        userRepository.remove(userId);
    }
}
