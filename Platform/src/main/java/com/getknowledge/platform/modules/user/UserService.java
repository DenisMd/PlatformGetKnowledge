package com.getknowledge.platform.modules.user;

import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.modules.Result;
import com.getknowledge.platform.modules.role.Role;
import com.getknowledge.platform.modules.role.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service("UserService")
public class UserService extends AbstractService {

    @Autowired
    private RoleRepository roleRepository;

    @Action(name = "updateUser" , mandatoryFields = {"userId"})
    @Transactional
    public Result updateUser(HashMap<String,Object> data) {

        User user = userRepository.read(longFromField("userId",data));
        if (user == null) return Result.NotFound();

        if (!isAccessToEdit(data,user)) {
            return Result.AccessDenied("Access denied for update sys user data");
        }

        if (data.containsKey("roleName")) {
            Role role =roleRepository.getSingleEntityByFieldAndValue("roleName",data.get("roleName"));
            if (role != null) {
                user.setRole(role);
            }
        }

        if (data.containsKey("enabled")) {
            Boolean enabled = (Boolean) data.get("enabled");
            user.setEnabled(enabled.booleanValue());
        }

        userRepository.merge(user);
        return Result.Complete();
    }

}
