package com.getknowledge.modules.programs.group;

import com.getknowledge.modules.section.Section;
import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("GroupProgramsRepository")
public class GroupProgramsRepository extends BaseRepository<GroupPrograms> {
    @Override
    protected Class<GroupPrograms> getClassEntity() {
        return GroupPrograms.class;
    }

    public GroupPrograms createGroupPrograms(String title,String url,Section section){
        GroupPrograms groupPrograms = new GroupPrograms();
        groupPrograms.setTitle(title);
        groupPrograms.setSection(section);
        groupPrograms.setUrl(url);
        create(groupPrograms);
        return groupPrograms;
    }
}
