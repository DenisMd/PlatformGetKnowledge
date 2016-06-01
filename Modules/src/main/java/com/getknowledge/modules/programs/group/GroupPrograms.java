package com.getknowledge.modules.programs.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.books.Book;
import com.getknowledge.modules.programs.Program;
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
import java.util.List;

@Entity
@Table(name = "program_groups",indexes = {@Index(name = "index_by_title",  columnList="title", unique = true)})
@ModuleInfo(repositoryName = "GroupProgramsRepository" , serviceName = "GroupProgramsService")
public class GroupPrograms extends Folder implements CloneableEntity<GroupPrograms> {

    @ManyToOne(optional = false)
    @ModelView(type = {ViewType.Public})
    private Section section;

    @Transient
    private long programsCount = 0;

    @OneToMany(mappedBy = "groupPrograms")
    @JsonIgnore
    private List<Program> programs;

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

    public List<Program> getPrograms() {
        return programs;
    }

    public void setPrograms(List<Program> programs) {
        this.programs = programs;
    }

    @Override
    public GroupPrograms clone() {
        GroupPrograms groupPrograms = new GroupPrograms();
        groupPrograms.setUrl(getUrl());
        groupPrograms.setTitle(getTitle());
        groupPrograms.setId(getId());
        groupPrograms.setObjectVersion(getObjectVersion());
        groupPrograms.setSection(getSection());
        groupPrograms.setCover(getCover());
        groupPrograms.setDescriptionEn(getDescriptionEn());
        groupPrograms.setDescriptionRu(getDescriptionRu());
        groupPrograms.setProgramsCount(getProgramsCount());
        groupPrograms.setCreateDate(getCreateDate());
        return groupPrograms;
    }
}
