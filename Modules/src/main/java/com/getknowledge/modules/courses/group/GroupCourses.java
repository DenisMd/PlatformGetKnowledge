package com.getknowledge.modules.courses.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.section.Section;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.base.entities.Folder;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.permission.names.PermissionNames;

import javax.persistence.*;

@Entity
@Table(name = "courses_group")
@ModuleInfo(repositoryName = "GroupCoursesRepository" , serviceName = "GroupCoursesService")
public class GroupCourses extends Folder {

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
