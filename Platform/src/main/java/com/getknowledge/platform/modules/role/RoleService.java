package com.getknowledge.platform.modules.role;

import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import com.getknowledge.platform.modules.permission.PermissionRepository;
import com.getknowledge.platform.modules.permission.names.PermissionNames;
import com.getknowledge.platform.modules.role.names.RoleName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service(value = "RoleService")
public class RoleService extends AbstractService implements BootstrapService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public void bootstrap(HashMap<String, Object> map) {
        if(roleRepository.count() == 0) {
            Role roleAdmin = new Role();
            roleAdmin.setRoleName(RoleName.ROLE_ADMIN.name());
            roleRepository.create(roleAdmin);

            Role roleUser = new Role();
            roleUser.setRoleName(RoleName.ROLE_USER.name());
            roleRepository.create(roleUser);

            Role roleHelpDesk = new Role();
            roleHelpDesk.setRoleName(RoleName.ROLE_HELPDESK.name());
            roleRepository.create(roleHelpDesk);

            Role author = new Role();
            author.setRoleName(RoleName.ROLE_AUTHOR.name());
            roleRepository.create(author);

            Role moderator = new Role();
            moderator.setRoleName(RoleName.ROLE_MODERATOR.name());
            moderator.getPermissions().add(permissionRepository.getPermissionByName(PermissionNames.EditSections));
            roleRepository.create(moderator);
        }
    }

    @Override
    public BootstrapInfo getBootstrapInfo() {
        BootstrapInfo bootstrapInfo = new BootstrapInfo();
        bootstrapInfo.setName("Role Service");
        return bootstrapInfo;
    }


}
