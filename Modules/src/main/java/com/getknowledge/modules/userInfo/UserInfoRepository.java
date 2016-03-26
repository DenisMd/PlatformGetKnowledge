package com.getknowledge.modules.userInfo;

import com.getknowledge.modules.menu.enumerations.MenuNames;
import com.getknowledge.modules.menu.MenuRepository;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.modules.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public UserInfo read(Long id) {
        UserInfo userInfo = super.read(id);
        if (userInfo == null)
            return null;
        if (currentUser != null && currentUser.getId().equals(userInfo.getUser().getId())) {
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
    @Transactional
    public void remove(Long id) throws PlatformException {
        //Пользователей не возможно удалить
    }

    public UserInfo getUserInfoByUser(com.getknowledge.platform.modules.user.User user) {
        if (user == null) return null;

        List<UserInfo> userInfo = entityManager.createQuery("select ui from UserInfo  ui where ui.user.id = :id")
                .setParameter("id" , user.getId()).getResultList();

        return userInfo.isEmpty() ? null : userInfo.get(0);
    }
}
