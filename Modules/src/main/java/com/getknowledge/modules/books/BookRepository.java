package com.getknowledge.modules.books;

import com.getknowledge.modules.books.group.GroupBooks;
import com.getknowledge.modules.books.tags.BooksTag;
import com.getknowledge.modules.books.tags.BooksTagRepository;
import com.getknowledge.modules.dictionaries.language.Language;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.platform.annotations.Filter;
import com.getknowledge.platform.base.repositories.FilterCountQuery;
import com.getknowledge.platform.base.repositories.FilterQuery;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

@Repository("BookRepository")
public class BookRepository extends ProtectedRepository<Book> {

    @Autowired
    private BooksTagRepository booksTagRepository;

    @Filter(name = "searchBooks")
    public void searchBook(HashMap<String,Object> data , FilterQuery<Book> query, FilterCountQuery<Book> countQuery) {
        Join join = query.getJoin(new String[]{"tags"},0,null,JoinType.LEFT);
        String value = (String) data.get("textValue");
        Predicate name = query.getCriteriaBuilder().like(query.getRoot().get("name"),"%"+value+"%");
        Predicate tags = query.getCriteriaBuilder().like(join.get("tagName"),"%"+value+"%");
        query.addPrevPredicate(query.getCriteriaBuilder().or(name,tags));


        Join join2 = countQuery.getJoin(new String[]{"tags"}, 0, null, JoinType.LEFT);
        Predicate name2 = countQuery.getCriteriaBuilder().like(countQuery.getRoot().get("name"),"%"+value+"%");
        Predicate tags2 = countQuery.getCriteriaBuilder().like(join2.get("tagName"),"%"+value+"%");
        countQuery.addPrevPredicate(countQuery.getCriteriaBuilder().or(name2,tags2));
    }

    @Override
    protected Class<Book> getClassEntity() {
        return Book.class;
    }

    private void addBookToTag(Book book) {
        for (BooksTag booksTag : book.getTags()) {
            booksTag.getBooks().add(book);
            booksTagRepository.merge(booksTag);
        }
    }

    public Book createBook(GroupBooks groupBooks,UserInfo owner, String name,String description,Language language,List<String> links,List<String> tags,byte [] cover) {
        Book book = new Book();
        book.setGroupBooks(groupBooks);
        book.setName(name);
        book.setDescription(description);
        book.setLanguage(language);
        book.setOwner(owner);
        if (links != null)
            book.setLinks(links);
        if (tags != null)
            booksTagRepository.createTags(tags,book);

        book.setCover(cover);
        book.setCreateDate(Calendar.getInstance());
        create(book);
        addBookToTag(book);
        return book;
    }

    public Book updateBook(Book book,String name,String description,List<String> links,List<String> tags) {
        book.setName(name);
        book.setDescription(description);
        if (links != null)
            book.setLinks(links);
        booksTagRepository.removeTagsFromEntity(book);
        if (tags != null && !tags.isEmpty()) {
            booksTagRepository.createTags(tags, book);
        }

        merge(book);
        if (tags != null  && !tags.isEmpty()) {
            addBookToTag(book);
        }
        booksTagRepository.removeUnusedTags();
        return book;
    }
}
