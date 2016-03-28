package com.getknowledge.modules.userInfo;

import com.getknowledge.modules.dictionaries.language.Language;
import com.getknowledge.modules.dictionaries.language.names.Languages;
import com.getknowledge.modules.menu.enumerations.MenuNames;
import com.getknowledge.modules.menu.MenuRepository;
import com.getknowledge.modules.userInfo.dialog.Dialog;
import com.getknowledge.modules.userInfo.dialog.DialogRepository;
import com.getknowledge.modules.userInfo.post.messages.PostMessage;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import com.getknowledge.platform.modules.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Repository("UserInfoRepository")
public class UserInfoRepository extends ProtectedRepository<UserInfo> {

    @Override
    protected Class<UserInfo> getClassEntity() {
        return UserInfo.class;
    }

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DialogRepository dialogRepository;

    @Override
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
    public void remove(UserInfo userInfo) {
        //Пользователей не возможно удалить если они активиравонны
        if (!userInfo.getUser().isEnabled()){
            userRepository.remove(userInfo.getUser());
            super.remove(userInfo);
        }
    }

    public UserInfo getCurrentUser(HashMap<String,Object> data){
        com.getknowledge.platform.modules.user.User user = userRepository.getCurrentUser(data);
        return getUserInfoByUser(user);
    }

    public UserInfo createUserInfo(com.getknowledge.platform.modules.user.User user, String firstName, String lastName, Language language, boolean man, byte [] profileImage){
        UserInfo userInfo = new UserInfo();
        userInfo.setUser(user);
        userInfo.setFirstName(firstName);
        userInfo.setLastName(lastName);
        userInfo.setLanguage(language);
        userInfo.setMan(man);
        userInfo.setFirstLogin(true);
        userInfo.setProfileImage(profileImage);
        create(userInfo);
        return userInfo;
    }

    public UserInfo getUserInfoByUser(com.getknowledge.platform.modules.user.User user) {
        if (user == null) return null;
        return getSingleEntityByFieldAndValue("user.id",user.getId());
    }

    public List<PostMessage> postMessages(UserInfo userInfo,int first,int max){
        List<PostMessage> messages = entityManager.createQuery("select pm from PostMessage pm " +
                "where pm.recipient.id = :userId order by pm.createTime desc")
                .setParameter("userId", userInfo.getId())
                .setFirstResult(first)
                .setMaxResults(max)
                .getResultList();
        return messages;
    }

    public Dialog getDialog(UserInfo current,UserInfo companion){
        for (Dialog dialog : current.getDialogs()) {
            if (dialog.getCompanion().equals(companion)){
                return dialog;
            }
        }

        Dialog dialog = new Dialog();
        dialog.setUser(current);
        dialog.setCompanion(companion);
        dialogRepository.create(dialog);
        return dialog;
    }
}
