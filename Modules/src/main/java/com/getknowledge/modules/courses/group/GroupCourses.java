package com.getknowledge.modules.courses.group;

import com.getknowledge.modules.section.Section;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.permission.names.PermissionNames;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "courses_group")
@ModuleInfo(repositoryName = "GroupCoursesRepository" , serviceName = "GroupCoursesService")
public class GroupCourses extends AbstractEntity {

    private String title;

    @Column(name = "description_en")
    private String descriptionEn;

    @Column(name = "description_ru")
    private String descriptionRu;

    @ManyToOne
    private Section section;

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

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
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
        al.getPermissionsForCreate().add(new Permission(PermissionNames.EditGroupCourses.getName()));
        al.getPermissionsForEdit().add(new Permission(PermissionNames.EditGroupCourses.getName()));
        al.getPermissionsForRemove().add(new Permission(PermissionNames.EditGroupCourses.getName()));
        return al;
    }
}
