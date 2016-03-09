package com.getknowledge.modules.dictionaries.knowledge;

import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.permission.names.PermissionNames;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "knowledge")
@ModuleInfo(repositoryName = "KnowledgeRepository" , serviceName = "KnowledgeService")
public class Knowledge extends AbstractEntity {

    @Column(name = "type")
    private KnowledgeType knowledgeType;

    private String name;

    @Column(length = 750)
    private String note;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public KnowledgeType getKnowledgeType() {
        return knowledgeType;
    }

    public void setKnowledgeType(KnowledgeType knowledgeType) {
        this.knowledgeType = knowledgeType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList authorizationList = new AuthorizationList();
        authorizationList.allowCreateEveryOne = false;
        authorizationList.allowReadEveryOne = true;

        authorizationList.getPermissionsForCreate().add(new Permission(PermissionNames.EditKnowledge));
        authorizationList.getPermissionsForEdit().add(new Permission(PermissionNames.EditKnowledge));
        authorizationList.getPermissionsForRemove().add(new Permission(PermissionNames.EditKnowledge));

        return authorizationList;
    }
}
