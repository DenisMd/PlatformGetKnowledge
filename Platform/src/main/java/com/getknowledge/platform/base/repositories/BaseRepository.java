package com.getknowledge.platform.base.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.exceptions.PlatformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;


@Transactional
public abstract class BaseRepository<T extends AbstractEntity> {

    protected abstract Class<T> getClassEntity();

    protected Logger logger = LoggerFactory.getLogger(BaseRepository.class);

    @PersistenceContext
    public EntityManager entityManager;

    public void create(T object) {
        ObjectMapper objectMapper = new ObjectMapper();
        if(object == null) {
            throw new NullPointerException();
        }

        entityManager.persist(object);
        entityManager.flush();
    }

    public void update(T object) {
        if(object == null) {
            throw new NullPointerException();
        }

        T classicObject = read(object.getId());

        if (classicObject == null)
            return;

        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors()) {
                if (pd.getReadMethod() != null && !"class".equals(pd.getName())) {
                    Object result = pd.getReadMethod().invoke(object);
                    if (result != null) {
                        pd.getWriteMethod().invoke(classicObject,result);
                    }
                }
            }
        } catch (Exception e) {
           logger.error(e.getMessage(),e);
        }

        entityManager.merge(classicObject);
        entityManager.flush();
    }

    public void remove(Long id) throws PlatformException {
        if(id == null) {
            throw new NullPointerException();
        }

        entityManager.remove(entityManager.find(getClassEntity(), id));
        entityManager.flush();
    }

    public T read(Long id) {
        if(id == null) {
            throw new NullPointerException();
        }
        T result = entityManager.find(getClassEntity() , id);
        return result;
    }

    public List<T> list() {
        List<T> list = (List<T>)entityManager.createQuery("Select t from " + getClassEntity().getSimpleName() + " t").getResultList();
        return list;
    }

    public List<T> listPartial(int first, int max) {
        Query query = entityManager.createQuery("Select t from " + getClassEntity().getSimpleName() + " t");
        query.setFirstResult(first);
        query.setMaxResults(max);
        List<T> list = (List<T>)query.getResultList();
        return list;
    }

    public Long count() {
        long rowCnt= (Long) entityManager.createQuery("SELECT count(a) FROM " + getClassEntity().getSimpleName() + " a").getSingleResult();
        return rowCnt;
    }

    public List<T> getEntitiesByFieldAndValue(String field, Object value) {
        List<T> list = (List<T>)entityManager.createQuery("select ent from " + getClassEntity().getSimpleName() + " ent where ent."+field+"=:value")
                .setParameter("value" , value).getResultList();
        return list;
    }

    public T getSingleEntityByFieldAndValue(String field, Object value) {
        List<T> list = getEntitiesByFieldAndValue(field,value);
        return list.isEmpty() ? null : list.get(0);
    }
}
