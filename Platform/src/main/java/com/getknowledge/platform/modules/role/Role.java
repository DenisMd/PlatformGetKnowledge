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
    @JoinTable(name = "permissions_of_roles")
    private List<Permission> permissions = new ArrayList<>();

    @Column
    private String note;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Role role = (Role) o;
        return roleName.equals(role.roleName);

    }

    @Override
    public int hashCode() {
        return roleName != null ? roleName.hashCode() : 0;
    }
}
