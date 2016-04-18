package com.getknowledge.modules.programs.tags;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.programs.Program;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.entities.AuthorizationList;
import com.getknowledge.platform.base.entities.ITag;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "program_tag")
public class ProgramTag extends AbstractEntity implements ITag {

    @Column(nullable = false)
    private String tagName;

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

    @Override
    public String getTagName() {
        return tagName;
    }

    @Override
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public AuthorizationList getAuthorizationList() {
        return null;
    }
}
