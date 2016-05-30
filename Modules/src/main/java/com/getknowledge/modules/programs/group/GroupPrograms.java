package com.getknowledge.modules.programs.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.section.Section;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.base.entities.Folder;
import com.getknowledge.platform.modules.permission.Permission;
import com.getknowledge.platform.modules.permission.names.PermissionNames;

import javax.persistence.*;

@Entity
@Table(name = "program_groups",indexes = {@Index(name = "index_by_title",  columnList="title", unique = true)})
@ModuleInfo(repositoryName = "GroupProgramsRepository" , serviceName = "GroupProgramsService")
public class GroupPrograms extends Folder {

    @ManyToOne(optional = false)
    private Section section;

    @Transient
    private long programsCount = 0;

    public long getProgramsCount() {
        return programsCount;
    }

    public void setProgramsCount(long programsCount) {
        this.programsCount = programsCount;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }
}
