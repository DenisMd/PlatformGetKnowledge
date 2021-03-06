package com.getknowledge.modules.dictionaries.programming.styles;

import com.getknowledge.modules.platform.auth.PermissionNames;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.modules.permission.Permission;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "programming_styles")
@ModuleInfo(repositoryName = "ProgrammingStyleRepository" , serviceName = "ProgrammingStyleService")
public class ProgrammingStyle extends AbstractEntity{

    @Column(nullable = false)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.allowReadEveryOne = true;
        authorizationList.getPermissionsForEdit().add(new Permission(PermissionNames.EditProgrammingDictionaries()));
        authorizationList.getPermissionsForCreate().add(new Permission(PermissionNames.EditProgrammingDictionaries()));
        authorizationList.getPermissionsForRemove().add(new Permission(PermissionNames.EditProgrammingDictionaries()));
        return authorizationList;
    }
}
