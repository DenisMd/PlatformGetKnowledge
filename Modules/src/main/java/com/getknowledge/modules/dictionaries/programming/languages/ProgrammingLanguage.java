package com.getknowledge.modules.dictionaries.programming.languages;

import com.getknowledge.modules.platform.auth.PermissionNames;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.modules.permission.Permission;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "programming_languages")
@ModuleInfo(repositoryName = "ProgrammingLanguageRepository" ,serviceName = "ProgrammingLanguageService")
public class ProgrammingLanguage extends AbstractEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "mode" , nullable = false)
    private String mode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String jsFile) {
        this.mode = jsFile;
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
