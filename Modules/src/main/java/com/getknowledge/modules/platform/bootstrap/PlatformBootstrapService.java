package com.getknowledge.modules.platform.bootstrap;

import com.getknowledge.modules.platform.auth.PermissionNames;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.permission.PermissionRepository;
import com.getknowledge.platform.modules.role.Role;
import com.getknowledge.platform.modules.role.RoleRepository;
import com.getknowledge.platform.modules.role.names.RoleName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service("PlatformBootstrapService")
public class PlatformBootstrapService implements BootstrapService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void bootstrap(HashMap<String, Object> map) throws Exception {
        if (permissionRepository.count() == 0) {
            permissionRepository.ifNotExistCreate(new Permission(PermissionNames.EditSocialLinks()));
            permissionRepository.ifNotExistCreate(new Permission(PermissionNames.EditMenu()));
            permissionRepository.ifNotExistCreate(new Permission(PermissionNames.EditSocialLinks()));
            permissionRepository.ifNotExistCreate(new Permission(PermissionNames.EditFolders()));
            permissionRepository.ifNotExistCreate(new Permission(PermissionNames.ReadHpMessage()));
            permissionRepository.ifNotExistCreate(new Permission(PermissionNames.EditBooks()));
            permissionRepository.ifNotExistCreate(new Permission(PermissionNames.CreateBooks()));
            permissionRepository.ifNotExistCreate(new Permission(PermissionNames.CreatePrograms()));
            permissionRepository.ifNotExistCreate(new Permission(PermissionNames.EditPrograms()));
            permissionRepository.ifNotExistCreate(new Permission(PermissionNames.EditProgrammingDictionaries()));
            permissionRepository.ifNotExistCreate(new Permission(PermissionNames.CreateCourse()));
            permissionRepository.ifNotExistCreate(new Permission(PermissionNames.EditCourse()));
            permissionRepository.ifNotExistCreate(new Permission(PermissionNames.EditKnowledge()));
            permissionRepository.ifNotExistCreate(new Permission(PermissionNames.UploadVideos()));
            permissionRepository.ifNotExistCreate(new Permission(PermissionNames.EditNews()));
            permissionRepository.ifNotExistCreate(new Permission(PermissionNames.BlockComments()));
        }


        if (roleRepository.count() == 0) {
            if(roleRepository.count() == 0) {
                Role roleAdmin = new Role();
                roleAdmin.setRoleName(RoleName.ROLE_ADMIN.name());
                roleRepository.create(roleAdmin);

                Role roleUser = new Role();
                roleUser.setRoleName(RoleName.ROLE_USER.name());
                roleRepository.create(roleUser);

                Role roleHelpDesk = new Role();
                roleHelpDesk.setRoleName(RoleName.ROLE_HELPDESK.name());
                roleHelpDesk.getPermissions().add(permissionRepository.getPermissionByName(PermissionNames.ReadHpMessage()));
                roleRepository.create(roleHelpDesk);

                Role author = new Role();
                author.setRoleName(RoleName.ROLE_AUTHOR.name());
                author.getPermissions().add(permissionRepository.getPermissionByName(PermissionNames.CreateBooks()));
                author.getPermissions().add(permissionRepository.getPermissionByName(PermissionNames.CreatePrograms()));
                author.getPermissions().add(permissionRepository.getPermissionByName(PermissionNames.CreateCourse()));
                author.getPermissions().add(permissionRepository.getPermissionByName(PermissionNames.UploadVideos()));
                roleRepository.create(author);

                Role moderator = new Role();
                moderator.setRoleName(RoleName.ROLE_MODERATOR.name());
                moderator.getPermissions().add(permissionRepository.getPermissionByName(PermissionNames.EditSections()));
                moderator.getPermissions().add(permissionRepository.getPermissionByName(PermissionNames.EditMenu()));
                moderator.getPermissions().add(permissionRepository.getPermissionByName(PermissionNames.EditSocialLinks()));
                moderator.getPermissions().add(permissionRepository.getPermissionByName(PermissionNames.EditFolders()));
                moderator.getPermissions().add(permissionRepository.getPermissionByName(PermissionNames.EditBooks()));
                moderator.getPermissions().add(permissionRepository.getPermissionByName(PermissionNames.EditProgrammingDictionaries()));
                moderator.getPermissions().add(permissionRepository.getPermissionByName(PermissionNames.EditPrograms()));
                moderator.getPermissions().add(permissionRepository.getPermissionByName(PermissionNames.EditCourse()));
                moderator.getPermissions().add(permissionRepository.getPermissionByName(PermissionNames.EditKnowledge()));
                moderator.getPermissions().add(permissionRepository.getPermissionByName(PermissionNames.BlockComments()));
                moderator.getPermissions().add(permissionRepository.getPermissionByName(PermissionNames.EditNews()));
                roleRepository.create(moderator);
            }
        }
    }

    @Override
    public BootstrapInfo getBootstrapInfo() {
        BootstrapInfo bootstrapInfo = new BootstrapInfo();
        bootstrapInfo.setName("Platform bootstrap");
        bootstrapInfo.setOrder(0);
        bootstrapInfo.setRepeat(false);
        return bootstrapInfo;
    }
}
