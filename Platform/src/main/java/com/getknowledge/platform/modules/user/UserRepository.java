package com.getknowledge.platform.modules.user;

import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.modules.role.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("UserRepository")
public class UserRepository extends BaseRepository<User> {

    @Autowired
    RoleRepository roleRepository;

    @Override
    @Transactional
    public void create(User object) {
        if (object == null) {
            throw new NullPointerException();
        }
        object.setEnabled(true);
        object.setHashPwd(object.getPwdTransient());
        super.create(object);
    }
}
