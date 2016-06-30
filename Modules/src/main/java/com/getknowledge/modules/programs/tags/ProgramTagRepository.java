package com.getknowledge.modules.programs.tags;

import com.getknowledge.modules.books.tags.BooksTag;
import com.getknowledge.modules.tags.TagRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ProgramTagRepository")
public class ProgramTagRepository extends TagRepository<ProgramTag>{

    @Override
    protected Class<ProgramTag> getClassEntity() {
        return ProgramTag.class;
    }

    @Override
    public void removeUnusedTags() {
        List<ProgramTag> list = entityManager.createQuery("select t from ProgramTag  t where t.programs is empty").getResultList();
        list.forEach((tag -> remove(tag.getId())));
    }

    @Override
    protected ProgramTag createTag() {
        return new ProgramTag();
    }
}
