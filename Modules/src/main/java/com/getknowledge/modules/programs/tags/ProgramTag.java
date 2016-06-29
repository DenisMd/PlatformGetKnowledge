package com.getknowledge.modules.programs.tags;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.programs.Program;
import com.getknowledge.modules.tags.Tag;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("program")
public class ProgramTag extends Tag {
    @ManyToMany
    @JoinTable(name = "tags_programs")
    @JsonIgnore
    private List<Program> programs = new ArrayList<>();

    public List<Program> getPrograms() {
        return programs;
    }

    public void setPrograms(List<Program> programs) {
        this.programs = programs;
    }
}
