package com.getknowledge.platform.modules.user;

import com.getknowledge.platform.base.repositories.BaseRepository;
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
    @Transactional
    public void create(User object) {
        if (object == null) {
            throw new NullPointerException();
        }
        if (object.getHashPwd() == null) {
            object.hashRawPassword(object.getPwdTransient());
        }
        super.create(object);
    }

    public User getCurrentUser(HashMap<String,Object> data){
        String login = (String) data.get("principalName");
        User user = getSingleEntityByFieldAndValue("login", login);
        return user;
    }
}
