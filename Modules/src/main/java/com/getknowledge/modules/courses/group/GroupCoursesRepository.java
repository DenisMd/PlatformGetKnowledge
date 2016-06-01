package com.getknowledge.modules.courses.group;

import com.getknowledge.modules.books.group.GroupBooks;
import com.getknowledge.modules.section.Section;
import com.getknowledge.platform.annotations.Filter;
import com.getknowledge.platform.annotations.ViewType;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.FilterQuery;
import com.getknowledge.platform.base.repositories.PrepareEntity;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import com.getknowledge.platform.modules.user.User;
import com.getknowledge.platform.utils.RepositoryUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

@Repository("GroupCoursesRepository")
public class GroupCoursesRepository extends ProtectedRepository<GroupCourses> {
    @Override
    protected Class<GroupCourses> getClassEntity() {
        return GroupCourses.class;
    }

    @Override
    public GroupCourses prepare(GroupCourses entity, User currentUser, List<ViewType> viewTypes) {
        Long coursesCount = (Long) entityManager.createQuery("select count(c) from Course c where c.groupCourses.id = :groupCoursesId")
        .setParameter("groupCoursesId" , entity.getId()).getSingleResult();
        entity.setCoursesCount(coursesCount);
        return super.prepare(entity,currentUser,viewTypes);
    }

    public GroupCourses createGroupCourses(String title, String url, Section section){
        GroupCourses groupCourses = new GroupCourses();
        groupCourses.setTitle(title);
        groupCourses.setSection(section);
        groupCourses.setUrl(url);
        groupCourses.setCreateDate(Calendar.getInstance());
        create(groupCourses);
        return groupCourses;
    }

    @Filter(name = "orderByCount")
    public void orderByCountCourses(HashMap<String,Object> data , FilterQuery<GroupBooks> query) {
        Join join = query.getRoot().join("courses", JoinType.LEFT);
        query.getCriteriaQuery().groupBy(query.getRoot().get("id"));
        query.getCriteriaQuery().orderBy(query.getCriteriaBuilder().desc(query.getCriteriaBuilder().count(join)));
    }
}
