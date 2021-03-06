package com.getknowledge.modules.userInfo;

import com.getknowledge.modules.courses.Course;
import com.getknowledge.modules.dictionaries.language.Language;
import com.getknowledge.modules.dictionaries.language.names.Languages;
import com.getknowledge.modules.menu.enumerations.MenuNames;
import com.getknowledge.modules.menu.MenuRepository;
import com.getknowledge.modules.userInfo.courseInfo.CourseInfo;
import com.getknowledge.modules.userInfo.courseInfo.CourseInfoRepository;
import com.getknowledge.modules.userInfo.dialog.Dialog;
import com.getknowledge.modules.userInfo.dialog.DialogRepository;
import com.getknowledge.modules.userInfo.post.messages.PostMessage;
import com.getknowledge.platform.annotations.ViewType;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import com.getknowledge.platform.base.repositories.enumerations.RepOperations;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import com.getknowledge.platform.modules.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository("UserInfoRepository")
public class UserInfoRepository extends ProtectedRepository<UserInfo> {

    @Override
    public List<RepOperations> restrictedOperations() {
        List<RepOperations> operations = new ArrayList<>();
        operations.add(RepOperations.Create);
        operations.add(RepOperations.Remove);
        operations.add(RepOperations.Update);
        return operations;
    }

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

    @Autowired
    private CourseInfoRepository courseInfoRepository;

    @Override
    public UserInfo prepare(UserInfo entity, com.getknowledge.platform.modules.user.User currentUser,List<ViewType> viewTypes) {

        //сохраняем идентификатор,так как при подготовке может обнулиться свойство user
        long userId         = entity.getUser().getId();
        String userLogin    = entity.getUser().getLogin();

        if (currentUser != null && currentUser.getId().equals(userId)) {
            entity.setUserMenu(menuRepository.getSingleEntityByFieldAndValue("name", MenuNames.AuthorizedUser.name()));
        } else {
            entity.setUserMenu(menuRepository.getSingleEntityByFieldAndValue("name", MenuNames.NotAuthorizedUser.name()));
        }

        List<User> list = ((List<User>)(List<?>)sessionRegistry.getAllPrincipals());
        entity.setOnline(list.stream().filter(
                userDetail -> userDetail.getUsername().equals(userLogin)
        ).findFirst().isPresent());


        return super.prepare(entity,currentUser,viewTypes);
    }

    @Override
    public void remove(UserInfo userInfo) {
        //Пользователей не возможно удалить если они активиравонны
        if (!userInfo.getUser().isEnabled()){
            super.remove(userInfo);
            userRepository.remove(userInfo.getUser());
        }
    }

    public UserInfo getCurrentUser(HashMap<String,Object> data){
        if (!data.containsKey("principalName"))
            return null;
        return findByLogin((String) data.get("principalName"));
    }

    public UserInfo findByLogin(String login){
        return getSingleEntityByFieldAndValue("user.login",login);
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

    public void startCourse(UserInfo userInfo, Course course){
        for (Course c : userInfo.getStudiedCourses()) {
            if (c.equals(course)){
                return;
            }
        }

        userInfo.getStudiedCourses().add(course);

        CourseInfo courseInfo = new CourseInfo();
        courseInfo.setUserInfo(userInfo);
        courseInfo.setCourse(course);
        courseInfoRepository.create(courseInfo);

        merge(userInfo);
        return;
    }
}
