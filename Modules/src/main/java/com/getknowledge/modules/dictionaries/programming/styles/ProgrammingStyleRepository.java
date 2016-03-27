package com.getknowledge.modules.dictionaries.programming.styles;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("ProgrammingStyleRepository")
public class ProgrammingStyleRepository extends BaseRepository<ProgrammingStyle> {

    @Override
    protected Class<ProgrammingStyle> getClassEntity() {
        return ProgrammingStyle.class;
    }

    public void create(String name){
        ProgrammingStyle programmingStyle = new ProgrammingStyle();
        programmingStyle.setName(name);
        create(programmingStyle);
    }
}
