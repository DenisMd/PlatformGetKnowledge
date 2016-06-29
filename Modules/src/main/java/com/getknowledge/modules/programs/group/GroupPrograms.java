package com.getknowledge.modules.programs.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.programs.Program;
import com.getknowledge.platform.annotations.ModuleInfo;
import com.getknowledge.modules.folder.Folder;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("programs")
@ModuleInfo(repositoryName = "GroupProgramsRepository", serviceName = "FolderService")
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
