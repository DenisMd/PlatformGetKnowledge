package com.getknowledge.modules.programs.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.programs.Program;
import com.getknowledge.modules.section.Section;
import com.getknowledge.platform.annotations.ModelView;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.platform.annotations.ViewType;
import com.getknowledge.platform.base.entities.CloneableEntity;
import com.getknowledge.modules.abs.entities.Folder;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("programs")
@ModuleInfo(repositoryName = "GroupProgramsRepository" , serviceName = "GroupProgramsService")
public class GroupPrograms extends Folder {

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

    public List<Program> getPrograms() {
        return programs;
    }

    public void setPrograms(List<Program> programs) {
        this.programs = programs;
    }

    @Override
    public Folder cloneFolder() {
        GroupPrograms groupPrograms = new GroupPrograms();
        groupPrograms.setProgramsCount(getProgramsCount());
        return groupPrograms;
    }
}
