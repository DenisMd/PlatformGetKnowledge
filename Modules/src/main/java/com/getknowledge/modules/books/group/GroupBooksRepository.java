package com.getknowledge.modules.books.group;

import com.getknowledge.modules.courses.group.GroupCourses;
import com.getknowledge.modules.section.Section;
import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("GroupBooksRepository")
public class GroupBooksRepository extends BaseRepository<GroupBooks> {
    @Override
    protected Class<GroupBooks> getClassEntity() {
        return GroupBooks.class;
    }

    public GroupBooks createGroupBook(String title,String url,Section section){
        GroupBooks groupBooks = new GroupBooks();
        groupBooks.setTitle(title);
        groupBooks.setSection(section);
        groupBooks.setUrl(url);
        create(groupBooks);
        return groupBooks;
    }
}
