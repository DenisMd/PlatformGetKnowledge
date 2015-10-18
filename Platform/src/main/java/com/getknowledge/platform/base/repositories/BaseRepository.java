package com.getknowledge.platform.base.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.getknowledge.platform.base.entities.AbstractEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public abstract class BaseRepository<T extends AbstractEntity> {

    protected Logger logger = LoggerFactory.getLogger(BaseRepository.class);

    @PersistenceContext
    public EntityManager entityManager;

    @Transactional
    public void create(T object) {
        ObjectMapper objectMapper = new ObjectMapper();
        if(object == null) {
            throw new NullPointerException();
        }

        object.setClassName(object.getClass().getName());
        entityManager.persist(object);
        entityManager.flush();
    }

    @Transactional
    public void update(T object) {
        if(object == null) {
            throw new NullPointerException();
        }

        entityManager.merge(object);
        entityManager.flush();
    }

    @Transactional
    public void remove(Long id , Class<T> classEntity) {
        if(id == null || classEntity == null) {
            throw new NullPointerException();
        }

        entityManager.remove(entityManager.find(classEntity, id));
        entityManager.flush();
    }

    @Transactional
    public T read(Long id , Class<T> classEntity) {
        if(id == null || classEntity == null) {
            throw new NullPointerException();
        }
        T result = entityManager.find(classEntity , id);
        return result;
    }

    @Transactional
    public List<T> list(Class<T> classEntity) {
        if( classEntity == null) {
            throw new NullPointerException();
        }
        List<T> list = entityManager.createQuery("Select t from " + classEntity.getSimpleName() + " t").getResultList();
        return list;
    }

    @Transactional
    public List<T> listPartial(Class<T> classEntity , int first, int max) {
        if( classEntity == null) {
            throw new NullPointerException();
        }
        Query query = entityManager.createQuery("Select t from " + classEntity.getSimpleName() + " t");
        query.setFirstResult(first);
        query.setMaxResults(max);
        List<T> list = query.getResultList();
        return list;
    }

    public Long count(Class<T> classEntity) {
        if( classEntity == null) {
            throw new NullPointerException();
        }
        long rowCnt= (Long) entityManager.createQuery("SELECT count(a) FROM " + classEntity.getSimpleName() + " a").getSingleResult();
        return rowCnt;
    }

    @Transactional
    public List<T> getEntitiesByFieldAndValue(Class<T> classEntity ,String field, Object value) {
        if( classEntity == null) {
            throw new NullPointerException();
        }
        List<T> list = entityManager.createQuery("select ent from " + classEntity.getSimpleName() + " ent where ent."+field+"=:value")
                .setParameter("value" , value).getResultList();
        return list;
    }

    @Transactional
    public T getSingleEntityByFieldAndValue(Class<T> classEntity ,String field, Object value) {
        if( classEntity == null) {
            throw new NullPointerException();
        }
        List<T> list = getEntitiesByFieldAndValue(classEntity,field,value);
        return list.isEmpty() ? null : list.get(0);
    }
}
