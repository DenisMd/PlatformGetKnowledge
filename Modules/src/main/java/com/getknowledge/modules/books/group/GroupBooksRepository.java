package com.getknowledge.modules.books.group;

import com.getknowledge.modules.courses.group.GroupCourses;
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

@Repository("GroupBooksRepository")
public class GroupBooksRepository extends ProtectedRepository<GroupBooks> {

    @Override
    public GroupBooks prepare(GroupBooks entity, User currentUser, List<ViewType> viewTypes) {
        Long booksCount = (Long) entityManager.createQuery("select count(b) from Book b where b.groupBooks.id = :groupBooksId")
                .setParameter("groupBooksId" , entity.getId()).getSingleResult();
        entity.setBooksCount(booksCount);
        return super.prepare(entity,currentUser,viewTypes);
    }

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
