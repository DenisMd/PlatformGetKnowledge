package com.getknowledge.modules.programs.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.modules.messages.Comment;
import com.getknowledge.modules.programs.Program;
import com.getknowledge.platform.annotations.ModuleInfo;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "program_comment")
@ModuleInfo(repositoryName = "ProgramCommentRepository" , serviceName = "ProgramCommentService")
public class ProgramComment extends Comment {

    @ManyToOne(optional = false)
    @JsonIgnore
    private Program program;

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    @Override
    protected Comment createComment() {
        return new ProgramComment();
    }
}
