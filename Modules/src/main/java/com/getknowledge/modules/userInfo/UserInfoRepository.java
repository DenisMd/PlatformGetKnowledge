package com.getknowledge.modules.userInfo;

import com.getknowledge.modules.menu.MenuNames;
import com.getknowledge.modules.menu.MenuRepository;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.modules.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("UserInfoRepository")
public class UserInfoRepository extends ProtectedRepository<UserInfo> {

    @Override
    protected Class<UserInfo> getClassEntity() {
        return UserInfo.class;
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Override
    public UserInfo read(Long id) {
        UserInfo userInfo = super.read(id);
        if (userInfo == null)
            return null;
        if (currentUser != null && currentUser.getId().equals(userInfo.getId())) {
            userInfo.setUserMenu(menuRepository.getSingleEntityByFieldAndValue("name", MenuNames.AuthorizedUser.name()));
        } else {
            userInfo.setUserMenu(menuRepository.getSingleEntityByFieldAndValue("name", MenuNames.NotAuthorizedUser.name()));
        }

        List<User> list = ((List<User>)(List<?>)sessionRegistry.getAllPrincipals());
        userInfo.setOnline(list.stream().filter(
                userDetail -> userDetail.getUsername().equals(userInfo.getUser().getLogin())
        ).findFirst().isPresent());
        return userInfo;
    }

    @Override
    public void remove(Long id) throws PlatformException {
        UserInfo userInfo = entityManager.find(getClassEntity() , id);
        long userId = userInfo.getUser().getId();
        super.remove(id);
        userRepository.remove(userId);
    }
}
