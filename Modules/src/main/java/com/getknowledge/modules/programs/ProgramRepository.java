package com.getknowledge.modules.programs;

import com.getknowledge.modules.books.Book;
import com.getknowledge.modules.dictionaries.language.Language;
import com.getknowledge.modules.programs.group.GroupPrograms;
import com.getknowledge.modules.programs.tags.ProgramTag;
import com.getknowledge.modules.programs.tags.ProgramTagRepository;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.platform.annotations.Filter;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.FilterCountQuery;
import com.getknowledge.platform.base.repositories.FilterQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.HashMap;
import java.util.List;

@Repository("ProgramRepository")
public class ProgramRepository extends BaseRepository<Program> {

    @Autowired
    private ProgramTagRepository programTagRepository;

    @Override
    protected Class<Program> getClassEntity() {
        return Program.class;
    }

    @Filter(name = "searchPrograms")
    public void searchPrograms(HashMap<String,Object> data , FilterQuery<Program> query, FilterCountQuery<Program> countQuery) {
        Join join = query.getJoin(new String[]{"tags"},0,null, JoinType.LEFT);
        String value = (String) data.get("textValue");
        Predicate name = query.getCriteriaBuilder().like(query.getRoot().get("name"),"%"+value+"%");
        Predicate tags = query.getCriteriaBuilder().like(join.get("tagName"),"%"+value+"%");
        query.addPrevPredicate(query.getCriteriaBuilder().or(name,tags));


        Join join2 = countQuery.getJoin(new String[]{"tags"}, 0, null, JoinType.LEFT);
        Predicate name2 = countQuery.getCriteriaBuilder().like(countQuery.getRoot().get("name"),"%"+value+"%");
        Predicate tags2 = countQuery.getCriteriaBuilder().like(join2.get("tagName"),"%"+value+"%");
        countQuery.addPrevPredicate(countQuery.getCriteriaBuilder().or(name2,tags2));
    }

    private void addProgramToTag(Program program) {
        for (ProgramTag programTag : program.getTags()) {
            programTag.getPrograms().add(program);
            programTagRepository.merge(programTag);
        }
    }

    public Program createProgram(GroupPrograms groupPrograms,UserInfo owner,String name,String description,Language language,List<String> links,List<String> tags) {
        Program program = new Program();
        program.setGroupPrograms(groupPrograms);
        program.setName(name);
        program.setOwner(owner);
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
        if (name != null)
            program.setName(name);
        if (description != null)
            program.setDescription(description);
        if (links != null)
            program.setLinks(links);

        programTagRepository.removeTagsFromEntity(program);
        if (tags != null  && !tags.isEmpty()) {
            programTagRepository.createTags(tags, program);
        }

        merge(program);
        if (tags != null  && !tags.isEmpty()) {
            addProgramToTag(program);
        }
        programTagRepository.removeUnusedTags();
        return program;
    }


}
