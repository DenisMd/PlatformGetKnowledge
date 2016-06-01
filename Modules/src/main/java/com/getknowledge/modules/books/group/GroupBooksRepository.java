package com.getknowledge.modules.books.group;

import com.getknowledge.modules.section.Section;
import com.getknowledge.platform.annotations.Filter;
import com.getknowledge.platform.annotations.ViewType;
import com.getknowledge.platform.base.repositories.FilterQuery;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import com.getknowledge.platform.modules.user.User;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.util.Calendar;
import java.util.HashMap;
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
        groupBooks.setCreateDate(Calendar.getInstance());
        create(groupBooks);
        return groupBooks;
    }

    @Filter(name = "orderByCount")
    public void orderByCountBooks(HashMap<String,Object> data , FilterQuery<GroupBooks> query) {
        Join join = query.getRoot().join("books", JoinType.LEFT);
        query.getCriteriaQuery().groupBy(query.getRoot().get("id"));
        query.getCriteriaQuery().orderBy(query.getCriteriaBuilder().desc(query.getCriteriaBuilder().count(join)));
    }
}
