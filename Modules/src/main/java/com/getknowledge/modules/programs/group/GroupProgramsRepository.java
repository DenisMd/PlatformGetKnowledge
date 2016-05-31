package com.getknowledge.modules.programs.group;

import com.getknowledge.modules.books.group.GroupBooks;
import com.getknowledge.modules.section.Section;
import com.getknowledge.platform.annotations.ViewType;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.PrepareEntity;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import com.getknowledge.platform.modules.user.User;
import com.getknowledge.platform.utils.RepositoryUtils;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.util.List;

@Repository("GroupProgramsRepository")
public class GroupProgramsRepository extends ProtectedRepository<GroupPrograms> {
    @Override
    protected Class<GroupPrograms> getClassEntity() {
        return GroupPrograms.class;
    }

    @Override
    public GroupPrograms prepare(GroupPrograms entity, User currentUser, List<ViewType> viewTypes) {
        Long programsCount = (Long) entityManager.createQuery("select count(p) from Program p where p.groupPrograms.id = :groupProgramsId")
                .setParameter("groupProgramsId" , entity.getId()).getSingleResult();
        entity.setProgramsCount(programsCount);
        return super.prepare(entity,currentUser,viewTypes);
    }

    public GroupPrograms createGroupPrograms(String title, String url, Section section){
        GroupPrograms groupPrograms = new GroupPrograms();
        groupPrograms.setTitle(title);
        groupPrograms.setSection(section);
        groupPrograms.setUrl(url);
        create(groupPrograms);
        return groupPrograms;
    }
}
