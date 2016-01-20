package com.getknowledge.modules.section;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.dictionaries.language.Language;
import com.getknowledge.modules.menu.item.MenuItem;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.permission.PermissionRepository;
import com.getknowledge.platform.modules.permission.names.PermissionNames;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;

@Entity
@Table(name = "section" , indexes =
        {@Index(name="index_by_name" , columnList = "name",unique = true)})
@ModuleInfo(repositoryName = "SectionRepository" , serviceName = "SectionService")
public class Section extends AbstractEntity {

    @Column
    private String name;

    @Column(columnDefinition = "Text" , name = "description_ru")
    private String descriptionRu;

    @Column(columnDefinition = "Text" , name = "description_en")
    private String descriptionEn;

    @OneToOne
    @JoinColumn(name = "menu_item")
    private MenuItem menuItem;

    @Basic(fetch=FetchType.LAZY)
    @Lob @Column(name="cover")
    @JsonIgnore
    private byte[] cover;

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

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
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
        authorizationList.allowReadEveryOne = true;
        authorizationList.getPermissionsForEdit().add(new Permission(PermissionNames.EditSections.getName()));
        return authorizationList;
    }
}