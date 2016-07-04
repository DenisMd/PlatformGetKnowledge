package com.getknowledge.modules.section;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.menu.item.MenuItem;
import com.getknowledge.modules.platform.auth.PermissionNames;
import com.getknowledge.platform.annotations.ModelView;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.annotations.ViewType;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.base.entities.CloneableEntity;
import com.getknowledge.platform.modules.permission.Permission;

import javax.persistence.*;

@Entity
@Table(name = "section")
@ModuleInfo(repositoryName = "SectionRepository" , serviceName = "SectionService")
public class Section extends AbstractEntity implements CloneableEntity<Section> {

    @Column(nullable = false)
    @ModelView(type = {ViewType.Public})
    private String name;

    @Column(columnDefinition = "Text" , name = "description_ru")
    private String descriptionRu;

    @Column(columnDefinition = "Text" , name = "description_en")
    private String descriptionEn;

    @OneToOne(optional = false)
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
        authorizationList.getPermissionsForEdit().add(new Permission(PermissionNames.EditSections()));
        return authorizationList;
    }

    @Override
    public Section clone() {
        Section section = new Section();
        section.setName(getName());
        section.setDescriptionEn(getDescriptionEn());
        section.setDescriptionRu(getDescriptionRu());
        section.setCover(getCover());
        section.setMenuItem(getMenuItem());
        section.setId(getId());
        section.setObjectVersion(getObjectVersion());
        return section;
    }
}