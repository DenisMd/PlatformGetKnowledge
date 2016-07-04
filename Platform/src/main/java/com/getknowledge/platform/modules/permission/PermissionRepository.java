package com.getknowledge.platform.modules.permission;

import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.modules.role.RoleRepository;
import com.getknowledge.platform.modules.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("PermissionRepository")
public class PermissionRepository extends BaseRepository<Permission> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    protected Class<Permission> getClassEntity() {
        return Permission.class;
    }

    public Permission ifNotExistCreate(Permission permission) {
        Permission finedPermission = getSingleEntityByFieldAndValue("permissionName", permission.getPermissionName());
        if (finedPermission == null) create(permission);
        return permission;
    }

    public Permission getPermissionByName (String permissionNames) {
        Permission permission = getSingleEntityByFieldAndValue("permissionName" , permissionNames);
        return permission;
    }

    @Override
    public void remove(Permission permission) {
        if (!permission.getUsers().isEmpty()) {
            permission.getUsers().forEach(u -> {
                u.getPermissions().remove(permission);
                userRepository.update(u);
            });
        }
        if (!permission.getRoles().isEmpty()) {
            permission.getRoles().forEach(r -> {
                r.getPermissions().remove(permission);
                roleRepository.update(r);
            });
        }
        super.remove(permission);
    }
}
