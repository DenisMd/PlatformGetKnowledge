package com.getknowledge.platform.base.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.permission.names.PermissionNames;

import javax.persistence.*;

@MappedSuperclass
public abstract class Folder extends AbstractEntity {

    @Column(nullable = false)
    private String title;

    @Column(name = "url" , unique = true)
    private String url;

    @Column(name = "description_en" , columnDefinition = "Text")
    private String descriptionEn;

    @Column(name = "description_ru" ,columnDefinition = "Text")
    private String descriptionRu;

    @Basic(fetch= FetchType.LAZY)
    @Lob
    @Column(name="cover")
    @JsonIgnore
    private byte[] cover;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {

        this.url = url.replace(" " , "");
    }

    public byte[] getCover() {
        return cover;
    }

    public void setCover(byte[] cover) {
        this.cover = cover;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getDescriptionRu() {
        return descriptionRu;
    }

    public void setDescriptionRu(String descriptionRu) {
        this.descriptionRu = descriptionRu;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        AuthorizationList al = new AuthorizationList();
        al.allowCreateEveryOne = false;
        al.allowReadEveryOne = true;
        al.getPermissionsForCreate().add(new Permission(PermissionNames.EditFolders.getName()));
        al.getPermissionsForEdit().add(new Permission(PermissionNames.EditFolders.getName()));
        al.getPermissionsForRemove().add(new Permission(PermissionNames.EditFolders.getName()));
        return al;
    }
}
