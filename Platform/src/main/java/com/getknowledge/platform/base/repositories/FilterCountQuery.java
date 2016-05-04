package com.getknowledge.platform.base.repositories;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;

public class FilterCountQuery<T> extends FilterQuery<T> {
    public FilterCountQuery(EntityManager entityManager , Class<T> classEntity) {
        criteriaBuilder = entityManager.getCriteriaBuilder();
        criteriaQuery = criteriaBuilder.createQuery(Long.class);
        root = criteriaQuery.from(classEntity);
        previousPredicate = criteriaBuilder.conjunction();
        this.entityManager = entityManager;
        pClassEntity = classEntity;
    }

    public Long getCount() {
        CriteriaQuery<Long> temp = criteriaQuery.select(criteriaBuilder.count(root)).where(previousPredicate);
        return entityManager.createQuery(temp).getSingleResult();
    }
}
