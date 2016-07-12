package com.getknowledge.modules.platform.bootstrap;

import com.getknowledge.modules.platform.auth.ModuleRoleName;
import com.getknowledge.modules.platform.auth.PermissionNames;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.permission.PermissionRepository;
import com.getknowledge.platform.modules.role.Role;
import com.getknowledge.platform.modules.role.RoleRepository;
import com.getknowledge.platform.modules.role.names.BaseRoleName;
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
        permissionRepository.ifNotExistCreate(new Permission(PermissionNames.EditSections()));
        permissionRepository.ifNotExistCreate(new Permission(PermissionNames.EditVideo()));
        permissionRepository.ifNotExistCreate(new Permission(PermissionNames.ReadVideo()));



        if (roleRepository.count() == 0) {
            if(roleRepository.count() == 0) {
                Role roleAdmin = new Role();
                roleAdmin.setRoleName(ModuleRoleName.ROLE_ADMIN());
                roleRepository.create(roleAdmin);

                Role roleUser = new Role();
                roleUser.setRoleName(ModuleRoleName.ROLE_USER());
                roleRepository.create(roleUser);

                Role roleHelpDesk = new Role();
                roleHelpDesk.setRoleName(ModuleRoleName.ROLE_HELPDESK());
                roleHelpDesk.getPermissions().add(permissionRepository.getPermissionByName(PermissionNames.ReadHpMessage()));
                roleRepository.create(roleHelpDesk);

                Role author = new Role();
                author.setRoleName(ModuleRoleName.ROLE_AUTHOR());
                author.getPermissions().add(permissionRepository.getPermissionByName(PermissionNames.CreateBooks()));
                author.getPermissions().add(permissionRepository.getPermissionByName(PermissionNames.CreatePrograms()));
                author.getPermissions().add(permissionRepository.getPermissionByName(PermissionNames.CreateCourse()));
                author.getPermissions().add(permissionRepository.getPermissionByName(PermissionNames.UploadVideos()));
                roleRepository.create(author);

                Role moderator = new Role();
                moderator.setRoleName(ModuleRoleName.ROLE_MODERATOR());
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
                moderator.getPermissions().add(permissionRepository.getPermissionByName(PermissionNames.EditVideo()));
                moderator.getPermissions().add(permissionRepository.getPermissionByName(PermissionNames.ReadVideo()));
                moderator.getPermissions().add(permissionRepository.getPermissionByName(PermissionNames.UploadVideos()));
                roleRepository.create(moderator);
            }
        }
    }

    @Override
    public BootstrapInfo getBootstrapInfo() {
        BootstrapInfo bootstrapInfo = new BootstrapInfo();
        bootstrapInfo.setName("Platform bootstrap");
        bootstrapInfo.setOrder(0);
        bootstrapInfo.setRepeat(true);
        return bootstrapInfo;
    }
}
