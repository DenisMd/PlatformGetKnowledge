package com.getknowledge.modules.dictionaries.language;

import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "language")
@ModuleInfo(repositoryName = "LanguageRepository" , serviceName = "LanguageService")
public class Language extends AbstractEntity {

    @Column(length = 5 , nullable = false)
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
        return authorizationList;
    }
}
