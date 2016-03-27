package com.getknowledge.platform.modules.user;

import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.modules.role.Role;
import com.getknowledge.platform.modules.role.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Repository("UserRepository")
public class UserRepository extends BaseRepository<User> {

    @Override
    protected Class<User> getClassEntity() {
        return User.class;
    }

    @Autowired
    RoleRepository roleRepository;

    @Override
    public void remove(Long id) {
        //Пользователей не возможно удалить из системы
        //Можно только блокировать
    }

    @Override
    public void remove(User entity) {
        //Пользователей не возможно удалить из системы
        //Можно только блокировать
    }

    @Override
    public void update(User object) {
        User user = read(object.getId());
        if (user != null){
            user.setEnabled(object.isEnabled());
            user.setRole(object.getRole());
            user.setPermissions(object.getPermissions());
            super.update(user);
        }
    }

    @Override
    public void create(User object) {
        if (object == null) {
            throw new NullPointerException();
        }
        if (object.getHashPwd() == null) {
            object.hashRawPassword(object.getPwdTransient());
        }
        super.create(object);
    }

    public User createUser(String login, String password, Role role, boolean enabled){
        User user = new User();
        user.setLogin(login);
        user.hashRawPassword(password);
        user.setRole(role);
        user.setEnabled(enabled);
        create(user);
        return user;
    }

    public User getCurrentUser(HashMap<String,Object> data){
        String login = (String) data.get("principalName");
        User user = getSingleEntityByFieldAndValue("login", login);
        return user;
    }
}
