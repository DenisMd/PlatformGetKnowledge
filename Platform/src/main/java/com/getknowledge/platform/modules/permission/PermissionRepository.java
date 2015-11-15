package com.getknowledge.platform.modules.permission;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("PermissionRepository")
public class PermissionRepository extends BaseRepository<Permission> {

    @Override
    protected Class<Permission> getClassEntity() {
        return Permission.class;
    }

    public Permission ifNotExistCreate(Permission permission) {
        Permission finedPermission = getSingleEntityByFieldAndValue("permissionName", permission.getPermissionName());
        if (finedPermission == null) create(permission);
        return permission;
    }
}
