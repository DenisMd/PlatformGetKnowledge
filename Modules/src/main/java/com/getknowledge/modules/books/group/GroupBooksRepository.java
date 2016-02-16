package com.getknowledge.modules.books.group;

import com.getknowledge.modules.courses.group.GroupCourses;
import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("GroupBooksRepository")
public class GroupBooksRepository extends BaseRepository<GroupBooks> {
    @Override
    protected Class<GroupBooks> getClassEntity() {
        return GroupBooks.class;
    }
}
