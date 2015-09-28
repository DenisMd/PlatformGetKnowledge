package com.getknowledge.platform.modules.role;

import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.modules.permission.Permission;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sys_role")
@ModuleInfo(repositoryName = "RoleRepository", serviceName = "RoleService")
public class Role extends AbstractEntity {

    @Column(name = "role_name" , unique = true)
    private String roleName;

    @OneToMany
    private List<Permission> permissions = new ArrayList<>();

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    /*
     * Изменять роли могут только администраторы
     * */
    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
