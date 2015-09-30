package com.getknowledge.platform.modules.permission;

import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "sys_permission")
@ModuleInfo(repositoryName = "PermissionRepository" , serviceName = "PermissionService")
public class Permission extends AbstractEntity{

    @Column(name =  "permission_name" , unique = true)
    private String permissionName;

    public Permission() {
        permissionName = "";
    }

    public Permission(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

//    Изменять права могут только администраторы
    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Permission that = (Permission) o;

        return permissionName.equals(that.permissionName);

    }

    @Override
    public int hashCode() {
        return permissionName.hashCode();
    }
}
