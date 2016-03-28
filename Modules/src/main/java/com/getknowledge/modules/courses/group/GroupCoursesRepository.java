package com.getknowledge.modules.courses.group;

import com.getknowledge.modules.books.group.GroupBooks;
import com.getknowledge.modules.section.Section;
import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("GroupCoursesRepository")
public class GroupCoursesRepository extends BaseRepository<GroupCourses> {
    @Override
    protected Class<GroupCourses> getClassEntity() {
        return GroupCourses.class;
    }

    public GroupCourses createGroupCourses(String title,String url,Section section){
        GroupCourses groupCourses = new GroupCourses();
        groupCourses.setTitle(title);
        groupCourses.setSection(section);
        groupCourses.setUrl(url);
        create(groupCourses);
        return groupCourses;
    }
}
