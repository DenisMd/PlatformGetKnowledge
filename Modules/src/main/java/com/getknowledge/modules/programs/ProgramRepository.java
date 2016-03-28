package com.getknowledge.modules.programs;

import com.getknowledge.modules.dictionaries.language.Language;
import com.getknowledge.modules.programs.group.GroupPrograms;
import com.getknowledge.modules.programs.tags.ProgramTag;
import com.getknowledge.modules.programs.tags.ProgramTagRepository;
import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ProgramRepository")
public class ProgramRepository extends BaseRepository<Program> {

    @Autowired
    private ProgramTagRepository programTagRepository;

    @Override
    protected Class<Program> getClassEntity() {
        return Program.class;
    }

    private void addProgramToTag(Program program) {
        for (ProgramTag programTag : program.getTags()) {
            programTag.getPrograms().add(program);
            programTagRepository.merge(programTag);
        }
    }

    public Program createProgram(GroupPrograms groupPrograms,String name,String description,Language language,List<String> links,List<String> tags) {
        Program program = new Program();
        program.setGroupPrograms(groupPrograms);
        program.setName(name);
        program.setDescription(description);
        program.setLanguage(language);
        if (links != null)
            program.setLinks(links);
        if (tags != null)
            programTagRepository.createTags(tags,program);

        create(program);
        addProgramToTag(program);
        return program;
    }

    public Program updateProgram(Program program,String name,String description,List<String> links,List<String> tags) {
        program.setName(name);
        program.setDescription(description);
        if (links != null)
            program.setLinks(links);
        if (tags != null) {
            programTagRepository.removeTagsFromEntity(program);
            programTagRepository.createTags(tags, program);
        }

        merge(program);
        addProgramToTag(program);
        programTagRepository.removeUnusedTags();
        return program;
    }


}
