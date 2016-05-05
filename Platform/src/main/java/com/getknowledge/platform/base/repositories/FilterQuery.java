package com.getknowledge.platform.base.repositories;

import com.getknowledge.platform.base.repositories.enumerations.OrderRoute;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.*;

public class FilterQuery<T> {
    //Задание свойст для фильтров
    protected Root<T> root = null;
    protected CriteriaQuery criteriaQuery = null;
    protected CriteriaBuilder criteriaBuilder = null;
    private List<Order> orders = null;
    protected Predicate previousPredicate = null;
    protected EntityManager entityManager;
    protected Class<T>  pClassEntity = null;
    private  boolean isConj = false;

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

    public void setLogicalExpression(String logicalExpression) {
        switch (logicalExpression) {
            case "and" :
                previousPredicate = criteriaBuilder.conjunction();
                isConj = true;
                break;
            case "or" :
                previousPredicate = criteriaBuilder.disjunction();
                isConj = false;
                break;
        }
    }

    public void setOrder (String orderField, OrderRoute order) {
        Path path = parseField(orderField);

        switch (order) {
            case Asc: orders.add(criteriaBuilder.asc(path)); break;
            case Desc: orders.add(criteriaBuilder.desc(path)); break;
        }
    }

    public void equal(String field, String value ,String type) {
        Path path = parseField(field);

        Comparable obj = (Comparable)convert(value,field,type);

        Predicate result = criteriaBuilder.equal(path,obj);

        addPrevPredicate(result);
    }

    public void like(String field, String value) {
        Path path = parseField(field);

        Predicate result = criteriaBuilder.like(path,value);

        addPrevPredicate(result);
    }

    public void greaterThan(String field, String value ,String type) {
        Path path = parseField(field);

        Comparable obj = (Comparable)convert(value,field,type);

        Predicate result = criteriaBuilder.greaterThan(path, obj);

        addPrevPredicate(result);
    }

    public void greaterThanOrEqualTo(String field, String value ,String type) {
        Path path = parseField(field);

        Comparable obj = (Comparable)convert(value,field,type);

        Predicate result = criteriaBuilder.greaterThanOrEqualTo(path,obj);

        addPrevPredicate(result);
    }

    public void lessThan(String field, String value ,String type) {
        Path path = parseField(field);

        Comparable obj = (Comparable)convert(value,field,type);

        Predicate result = criteriaBuilder.lessThan(path,obj);

        addPrevPredicate(result);
    }

    public void lessThanOrEqualTo(String field, String value ,String type) {
        Path path = parseField(field);

        Comparable obj = (Comparable)convert(value,field,type);

        Predicate result = criteriaBuilder.lessThanOrEqualTo(path, obj);

        addPrevPredicate(result);
    }

    public void between(String field, List<String> values ,String type) {
        Path path = parseField(field);

        Comparable [] objs = new Comparable[2];
        objs[0] = (Comparable) convert(values.get(0),field,type);
        objs[1] = (Comparable) convert(values.get(1),field,type);

        Predicate result = criteriaBuilder.between(path, objs[0], objs[1]);

        addPrevPredicate(result);
    }




    public void in (String  field, List<String> values,String type) {

        Path path = parseField(field);

        List<Object> objects = new ArrayList<>();

        for (String value : values) {
            objects.add(convert(value,field,type));
        }

        Predicate result = path.in(objects);

        addPrevPredicate(result);
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

    private Enum convertStringToEnum(String fieldName , String value) {
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

    private Field getField(String name , Class<?> type) throws NoSuchFieldException {

        for (Field field : type.getDeclaredFields()) {
            if (field.getName().equals(name)) {
                return field;
            }
        }

        if (type.getSuperclass() != null) {
            return  getField(name,type.getSuperclass());
        }

        throw new NoSuchFieldException("Field is not exist " +name);
    }


    private Class getType(String fieldName) {
        try {
            if (fieldName.contains(".")) {
                String [] split = fieldName.split("\\.");
                Class type = getField(split[0],pClassEntity).getType();
                for (int i=1; i < split.length;i++) {
                    type = getField(split[i],type).getType();
                }
                return type;
            }
            Field result =getField(fieldName, pClassEntity);
            return result.getType();
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    private Object convert(String str,String fieldName,String type) {
        Class clazz = getType(fieldName);
        if (clazz == null) return str;

        if (clazz.isEnum()) {
            return convertStringToEnum(fieldName, str);
        }

        if (type.equals("date")) {
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTimeInMillis(Long.parseLong(str));
            return cal;
        }

        if (clazz.getName().equals("java.lang.Integer") || clazz.getName().equals("int")) {
            return Integer.parseInt(str);
        } else if (clazz.getName().equals("java.lang.Long") || clazz.getName().equals("long")){
            return Long.parseLong(str);
        } else if (clazz.getName().equals("java.lang.Boolean") || clazz.getName().equals("boolean")){
            return Boolean.parseBoolean(str);
        }

        return str;
    }

    private void addPrevPredicate(Predicate result) {
        if (previousPredicate != null) {
            if (isConj)
                result = criteriaBuilder.and(previousPredicate, result);
            else
                result = criteriaBuilder.or(previousPredicate,result);
        }

        previousPredicate = result;
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
}
