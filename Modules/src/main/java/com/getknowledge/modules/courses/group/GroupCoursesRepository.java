package com.getknowledge.modules.courses.group;

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
        create(groupCourses);
        return groupCourses;
    }
}
