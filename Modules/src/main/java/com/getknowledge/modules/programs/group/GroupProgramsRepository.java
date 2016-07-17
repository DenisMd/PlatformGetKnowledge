package com.getknowledge.modules.programs.group;

import com.getknowledge.modules.books.group.GroupBooks;
import com.getknowledge.modules.section.Section;
import com.getknowledge.platform.annotations.Filter;
import com.getknowledge.platform.annotations.ViewType;
import com.getknowledge.platform.base.repositories.*;
import com.getknowledge.platform.modules.user.User;
import com.getknowledge.platform.utils.RepositoryUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.HashMap;
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
        groupPrograms.setCreateDate(Calendar.getInstance());
        create(groupPrograms);
        return groupPrograms;
    }

    @Filter(name = "orderByCount")
    public void orderByCountPrograms(HashMap<String,Object> data , FilterQuery<GroupBooks> query, FilterCountQuery<GroupPrograms> countQuery) {
        Join join = query.getRoot().join("programs", JoinType.LEFT);
        query.getCriteriaQuery().groupBy(query.getRoot().get("id"));
        boolean desc = (boolean) data.get("desc");
        if (desc) {
            query.getCriteriaQuery().orderBy(query.getCriteriaBuilder().desc(query.getCriteriaBuilder().count(join)));
        } else {
            query.getCriteriaQuery().orderBy(query.getCriteriaBuilder().asc(query.getCriteriaBuilder().count(join)));
        }
    }
}
