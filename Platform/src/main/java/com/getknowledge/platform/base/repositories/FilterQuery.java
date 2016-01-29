package com.getknowledge.platform.base.repositories;

import com.getknowledge.platform.base.repositories.enumerations.OrderRoute;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class FilterQuery<T> {
    //Задание свойст для фильтров
    private Root<T> root = null;
    private CriteriaQuery<T> q = null;
    private CriteriaBuilder cb = null;
    private List<Order> orders = null;
    private Predicate previous = null;
    public EntityManager entityManager;

    public FilterQuery(EntityManager entityManager , Class<T> classEntity) {
        cb = entityManager.getCriteriaBuilder();
        q = cb.createQuery(classEntity);
        root = q.from(classEntity);
        orders = new ArrayList<>();
        previous = null;
        this.entityManager = entityManager;
    }

    private Path parseField(String field) {
        Path path = null;

        if (field.contains(".")) {

            String [] split = field.split("\\.");
            Join join = root.join(split[0],JoinType.INNER);

            for (int i =1; i < split.length-1; i++) {
                join = join.join(split[i],JoinType.INNER);
            }

            path = join.get(split[split.length-1]);

        } else {
            path = root.get(field);
        }
        return path;
    }

    public void setOrder (String orderField, OrderRoute order) {
        Path path = parseField(orderField);

        switch (order) {
            case Asc: orders.add(cb.asc(path)); break;
            case Desc: orders.add(cb.desc(path)); break;
        }
    }

    public void searchText (String [] field, String [] values, boolean or) {

        List<Predicate> likes = new ArrayList<>();

        for (int i =0; i < field.length; i++) {
            Path path = parseField(field[i]);
            likes.add(cb.like(path,"%"+values[i]+"%"));
        }

        Predicate result = null;

        if (likes.size() == 1) {
            result = likes.get(0);
        } else {
            result = likes.get(0);
            for (int i = 1; i < likes.size(); i++) {
                if (or)
                    result = cb.or(likes.get(i) , result);
                else
                    result = cb.and(likes.get(i) , result);
            }
        }

        if (previous != null) {
            result = cb.and(previous , result);
        }

        previous = result;
    }

    public void in (String  field, List values) {

        Path path = parseField(field);

        Predicate inP = path.in(values);

        if (previous != null) {
            inP = cb.and(previous , inP);
        }

        previous = inP;
    }

    public void betweenDates(String field, Date start, Date end) {
        Path path = parseField(field);

        Predicate result = cb.between(path,start,end);

        if (previous != null)
            result = cb.and(previous,result);

        previous = result;
    }

    public void equal(String field, Object value) {
        Path path = parseField(field);

        Predicate result = cb.equal(path,value);

        if (previous != null)
            result = cb.and(previous,result);

        previous = result;
    }

    public Query getQuery(int first , int max) {

        if (!orders.isEmpty()) {
            q.orderBy(orders);
        }

        if (previous != null)
            q.where(previous);

        Query query = entityManager.createQuery(q);
        if (max > 0) {
            query.setFirstResult(first);
            query.setMaxResults(max);
        }

        return query;
    }

    public Root<T> getRoot() {
        return root;
    }
}
