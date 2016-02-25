package com.getknowledge.modules.dictionaries.programming.languages;

import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.permission.names.PermissionNames;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "programming_languages")
@ModuleInfo(repositoryName = "ProgrammingLanguageRepository" ,serviceName = "ProgrammingLanguageService")
public class ProgrammingLanguage extends AbstractEntity {

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
        authorizationList.getPermissionsForEdit().add(new Permission(PermissionNames.EditProgrammingDictionaries.getName()));
        authorizationList.getPermissionsForCreate().add(new Permission(PermissionNames.EditProgrammingDictionaries.getName()));
        authorizationList.getPermissionsForRemove().add(new Permission(PermissionNames.EditProgrammingDictionaries.getName()));
        return authorizationList;
    }
}
