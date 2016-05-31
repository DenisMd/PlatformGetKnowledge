package com.getknowledge.modules.courses.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.section.Section;
import com.getknowledge.platform.annotations.ModelView;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.annotations.ViewType;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.base.entities.CloneableEntity;
import com.getknowledge.platform.base.entities.Folder;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.permission.names.PermissionNames;

import javax.persistence.*;

@Entity
@Table(name = "courses_group")
@ModuleInfo(repositoryName = "GroupCoursesRepository" , serviceName = "GroupCoursesService")
public class GroupCourses extends Folder implements CloneableEntity<GroupCourses> {

    @ManyToOne(optional = false)
    @ModelView(type = {ViewType.Public})
    private Section section;

    @Transient
    private long coursesCount = 0;

    public long getCoursesCount() {
        return coursesCount;
    }

    public void setCoursesCount(long coursesCount) {
        this.coursesCount = coursesCount;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    @Override
    public GroupCourses clone() {
        GroupCourses groupCourses = new GroupCourses();
        groupCourses.setUrl(getUrl());
        groupCourses.setTitle(getTitle());
        groupCourses.setId(getId());
        groupCourses.setObjectVersion(getObjectVersion());
        groupCourses.setSection(getSection());
        groupCourses.setCover(getCover());
        groupCourses.setDescriptionEn(getDescriptionEn());
        groupCourses.setDescriptionRu(getDescriptionRu());
        groupCourses.setCoursesCount(getCoursesCount());
        groupCourses.setCreateDate(getCreateDate());
        return groupCourses;
    }
}
