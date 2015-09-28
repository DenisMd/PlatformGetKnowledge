package com.getknowledge.platform.modules.role;

import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import com.getknowledge.platform.modules.role.names.RoleName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service(value = "RoleService")
public class RoleService extends AbstractService implements BootstrapService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void bootstrap(HashMap<String, Object> map) {
        if(roleRepository.count(Role.class) == 0) {
            Role roleAdmin = new Role();
            roleAdmin.setRoleName(RoleName.ROLE_ADMIN.name());
            roleRepository.create(roleAdmin);

            Role roleUser = new Role();
            roleUser.setRoleName(RoleName.ROLE_USER.name());
            roleRepository.create(roleUser);
        }
    }

    @Override
    public BootstrapInfo getBootstrapInfo() {
        BootstrapInfo bootstrapInfo = new BootstrapInfo();
        bootstrapInfo.setName("Role Service");
        return bootstrapInfo;
    }


}
