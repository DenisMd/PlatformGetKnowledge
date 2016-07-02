package com.getknowledge.modules.folder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.section.Section;
import com.getknowledge.platform.annotations.ModelView;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.annotations.ViewType;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.base.entities.CloneableEntity;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.permission.names.PermissionNames;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Table(name = "folders")
@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
@DiscriminatorColumn( name = "type" )
@ModuleInfo(serviceName = "FolderService" , repositoryName = "FolderRepository")
public abstract class Folder extends AbstractEntity implements CloneableEntity<Folder> {

    @ManyToOne(optional = false)
    @ModelView(type = {ViewType.Public})
    private Section section;

    @Column(nullable = false)
    @ModelView(type = {ViewType.Public})
    private String title;

    @Column(name = "url" , unique = true)
    @ModelView(type = {ViewType.Public})
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

    @Column(name = "create_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createDate;

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public Calendar getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Calendar createDate) {
        this.createDate = createDate;
    }

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

    public abstract Folder cloneFolder();

    @Override
    public Folder clone() {
        Folder groupBooks = cloneFolder();
        groupBooks.setUrl(getUrl());
        groupBooks.setTitle(getTitle());
        groupBooks.setId(getId());
        groupBooks.setObjectVersion(getObjectVersion());
        groupBooks.setSection(getSection());
        groupBooks.setCover(getCover());
        groupBooks.setDescriptionEn(getDescriptionEn());
        groupBooks.setDescriptionRu(getDescriptionRu());
        groupBooks.setCreateDate(getCreateDate());
        return groupBooks;
    }

}
