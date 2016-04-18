package com.getknowledge.modules.dictionaries.knowledge;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.dictionaries.knowledge.enumeration.KnowledgeType;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.permission.names.PermissionNames;

import javax.persistence.*;

@Entity
@Table(name = "knowledge")
@ModuleInfo(repositoryName = "KnowledgeRepository" , serviceName = "KnowledgeService")
public class Knowledge extends AbstractEntity {

    @Column(name = "type" , nullable = false)
    @Enumerated(EnumType.STRING)
    private KnowledgeType knowledgeType;

    @Column(nullable = false)
    private String name;

    @Column(length = 750)
    private String note;

    @Basic(fetch= FetchType.LAZY)
    @Lob @Column(name="image")
    @JsonIgnore
    private byte[] image;

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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
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
