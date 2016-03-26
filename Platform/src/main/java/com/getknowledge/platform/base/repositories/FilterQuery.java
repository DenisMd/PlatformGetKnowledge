package com.getknowledge.platform.base.repositories;

import com.getknowledge.platform.base.repositories.enumerations.OrderRoute;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FilterQuery<T> {
    //Задание свойст для фильтров
    protected Root<T> root = null;
    protected CriteriaQuery criteriaQuery = null;
    protected CriteriaBuilder criteriaBuilder = null;
    private List<Order> orders = null;
    protected Predicate previousPredicate = null;
    protected EntityManager entityManager;
    protected Class<T>  pClassEntity = null;

    public FilterQuery(){

    }

    public FilterQuery(EntityManager entityManager , Class<T> classEntity) {
        criteriaBuilder = entityManager.getCriteriaBuilder();
        criteriaQuery = criteriaBuilder.createQuery(classEntity);
        root = criteriaQuery.from(classEntity);
        orders = new ArrayList<>();
        previousPredicate = criteriaBuilder.conjunction();
        this.entityManager = entityManager;
        pClassEntity = classEntity;
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
            case Asc: orders.add(criteriaBuilder.asc(path)); break;
            case Desc: orders.add(criteriaBuilder.desc(path)); break;
        }
    }

    public void searchText (String [] field, String [] values, boolean or) {

        List<Predicate> likes = new ArrayList<>();

        for (int i =0; i < field.length; i++) {
            Path path = parseField(field[i]);
            likes.add(criteriaBuilder.like(path,"%"+values[i]+"%"));
        }

        Predicate result = null;

        if (likes.size() == 1) {
            result = likes.get(0);
        } else {
            result = likes.get(0);
            for (int i = 1; i < likes.size(); i++) {
                if (or)
                    result = criteriaBuilder.or(likes.get(i) , result);
                else
                    result = criteriaBuilder.and(likes.get(i) , result);
            }
        }

        if (previousPredicate != null) {
            result = criteriaBuilder.and(previousPredicate, result);
        }

        previousPredicate = result;
    }

    private Enum covertStringToEnum(String fieldName , String value) {
        try {
            if (fieldName.contains(".")) {
                String [] split = fieldName.split("\\.");
                Class type = pClassEntity.getDeclaredField(split[0]).getType();
                for (int i=1; i < split.length;i++) {
                    type = type.getClass().getDeclaredField(split[i]).getType();
                }
                return Enum.valueOf((Class<Enum>) type, value);
            }
            Enum result = Enum.valueOf((Class<Enum>) pClassEntity.getDeclaredField(fieldName).getType(), value);
            return result;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    private boolean isEnum(String fieldName) {

        try {
            if (fieldName.contains(".")) {
                String [] split = fieldName.split("\\.");
                Class type = pClassEntity.getDeclaredField(split[0]).getType();
                for (int i=1; i < split.length;i++) {
                    type = type.getClass().getDeclaredField(split[i]).getType();
                }
                return type.isEnum();
            }
            Field result = pClassEntity.getDeclaredField(fieldName);
            return result.getType().isEnum();
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    public void in (String  field, List values) {

        Path path = parseField(field);

        if (isEnum(field)) {
            List enumList = new ArrayList<>();
            for (Object value : values) {
                if (value == null || ((String)value).isEmpty()) {
                    continue;
                }
                enumList.add(covertStringToEnum(field, (String) value));
            }
            if (enumList.isEmpty())
                return;
            values = enumList;
        }

        Predicate inP = path.in(values);

        if (previousPredicate != null) {
            inP = criteriaBuilder.and(previousPredicate, inP);
        }

        previousPredicate = inP;
    }

    public void betweenDates(String field, Date start, Date end) {
        Path path = parseField(field);

        Predicate result = criteriaBuilder.between(path,start,end);

        if (previousPredicate != null)
            result = criteriaBuilder.and(previousPredicate,result);

        previousPredicate = result;
    }

    public void equal(String field, Object value) {
        Path path = parseField(field);

        if (isEnum(field)) {
            if (value == null || ((String)value).isEmpty()) {
                return;
            }
            value = covertStringToEnum(field, (String) value);
        }

        Predicate result = criteriaBuilder.equal(path,value);

        if (previousPredicate != null)
            result = criteriaBuilder.and(previousPredicate,result);

        previousPredicate = result;
    }

    public Query getQuery(int first , int max) {

        if (!orders.isEmpty()) {
            criteriaQuery.orderBy(orders);
        }

        if (previousPredicate != null)
            criteriaQuery.where(previousPredicate);

        Query query = entityManager.createQuery(criteriaQuery);
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
