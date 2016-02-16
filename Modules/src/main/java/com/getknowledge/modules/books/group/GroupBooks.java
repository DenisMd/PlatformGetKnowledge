package com.getknowledge.modules.books.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.section.Section;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.base.entities.Folder;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.permission.names.PermissionNames;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "books_group",indexes = {@Index(name = "index_by_title",  columnList="title", unique = true)})
@ModuleInfo(repositoryName = "GroupBooksRepository" , serviceName = "GroupBooksService")
public class GroupBooks extends Folder {

    @ManyToOne
    @JsonIgnore
    private Section section;

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
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
