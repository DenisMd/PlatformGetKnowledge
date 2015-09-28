package com.getknowledge.platform.modules.permission;

import com.getknowledge.platform.base.repositories.AbstractRepository;
import org.springframework.stereotype.Repository;

@Repository("PermissionRepository")
public class PermissionRepository extends AbstractRepository<Permission> {

    public Permission ifNotExistCreate(Permission permission) {
        Permission finedPermission = getSingleEntityByFieldAndValue(Permission.class, "permissionName", permission.getPermissionName());
        if (finedPermission == null) create(permission);
        return permission;
    }
}
