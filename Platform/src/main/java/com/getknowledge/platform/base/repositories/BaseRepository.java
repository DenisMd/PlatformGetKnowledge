package com.getknowledge.platform.base.repositories;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.repositories.enumerations.*;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import com.getknowledge.platform.modules.user.User;
import com.getknowledge.platform.utils.ModuleLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import javax.persistence.criteria.Order;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseRepository<T extends AbstractEntity> {

    @Autowired
    protected TraceService trace;

    protected abstract Class<T> getClassEntity();

    @PersistenceContext
    public EntityManager entityManager;

    private final int ENTITY_LIMIT = 500;

    public long getMaxCountsEntities() {
        return ENTITY_LIMIT;
    }

    @Transactional
    public void create(T object) {
        ObjectMapper objectMapper = new ObjectMapper();
        if(object == null) {
            throw new NullPointerException();
        }

        entityManager.persist(object);
        entityManager.flush();
    }

    @Transactional
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
                        if (pd.getWriteMethod() != null)
                            pd.getWriteMethod().invoke(classicObject,result);
                    }
                }
            }
        } catch (Exception e) {
            trace.logException("Error update entity : " + e.getMessage(), e, TraceLevel.Error);
            return;
        }

        entityManager.merge(classicObject);
        entityManager.flush();
    }

    @Transactional
    public void merge(T object) {
        if(object == null) {
            throw new NullPointerException();
        }

        entityManager.merge(object);
        entityManager.flush();
    }

    @Transactional
    public void remove(Long id) throws PlatformException {
        if(id == null) {
            throw new NullPointerException();
        }

        entityManager.remove(entityManager.find(getClassEntity(), id));
        entityManager.flush();
    }

    @Transactional
    public T read(Long id) {
        if(id == null) {
            throw new NullPointerException();
        }
        T result = entityManager.find(getClassEntity() , id);
        return result;
    }

    @Transactional
    public List<T> list() {
        List<T> list = (List<T>)entityManager.createQuery("Select t from " + getClassEntity().getSimpleName() + " t order by t.id").getResultList();
        return list;
    }

    @Transactional
    public List<T> listPartial(int first, int max) {
        Query query = entityManager.createQuery("Select t from " + getClassEntity().getSimpleName() + " t order by t.id");
        query.setFirstResult(first);
        query.setMaxResults(max);
        List<T> list = (List<T>)query.getResultList();
        return list;
    }

    @Transactional
    public Long count() {
        long rowCnt= (Long) entityManager.createQuery("SELECT count(a) FROM " + getClassEntity().getSimpleName() + " a").getSingleResult();
        return rowCnt;
    }

    @Transactional
    public List<T> getEntitiesByFieldAndValue(String field, Object value) {
        List<T> list = (List<T>)entityManager.createQuery("select ent from " + getClassEntity().getSimpleName() + " ent where ent."+field+"=:value")
                .setParameter("value" , value).getResultList();
        return list;
    }

    @Transactional
    public T getSingleEntityByFieldAndValue(String field, Object value) {
        List<T> list = getEntitiesByFieldAndValue(field,value);
        return list.isEmpty() ? null : list.get(0);
    }


    public FilterQuery<T> initFilter() {
        FilterQuery<T> filterQuery = new FilterQuery<>(entityManager,getClassEntity());
        return filterQuery;
    }

    @Transactional
    public AbstractEntity prepare(AbstractEntity entity, BaseRepository repository, User currentUser, ModuleLocator moduleLocator) throws Exception {
        if (repository instanceof PrepareEntity) {

            if (repository instanceof ProtectedRepository) {
                ProtectedRepository protectedRepository = (ProtectedRepository) repository;
                protectedRepository.setCurrentUser(currentUser);
            }

            properties : for (PropertyDescriptor pd : Introspector.getBeanInfo(entity.getClass()).getPropertyDescriptors()) {
                if (pd.getReadMethod() != null && !"class".equals(pd.getName())) {
                    Object result = pd.getReadMethod().invoke(entity);
                    if (result == null) continue;
                    if (result instanceof AbstractEntity) {
                        for (Annotation annotation : pd.getReadMethod().getAnnotations()) {
                            if (annotation instanceof JsonIgnore) {
                                continue properties;
                            }
                        }

                        AbstractEntity abstractEntity = (AbstractEntity) result;
                        BaseRepository<AbstractEntity> repository2 = (BaseRepository<AbstractEntity>)moduleLocator.findRepository(abstractEntity.getClass());
                        if(pd.getWriteMethod() != null)
                            pd.getWriteMethod().invoke(entity, prepare(abstractEntity,repository2,currentUser,moduleLocator));
                    }
                }
            }


            return ((PrepareEntity) repository).prepare(entity);
        }
        return entity;
    }
}
