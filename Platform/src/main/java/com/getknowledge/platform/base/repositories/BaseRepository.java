package com.getknowledge.platform.base.repositories;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.getknowledge.platform.annotations.ModelView;
import com.getknowledge.platform.annotations.ViewType;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.repositories.enumerations.RepOperations;
import com.getknowledge.platform.exceptions.PlatformException;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import com.getknowledge.platform.modules.user.User;
import com.getknowledge.platform.utils.ModuleLocator;
import com.getknowledge.platform.utils.RepositoryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.swing.text.View;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseRepository<T extends AbstractEntity> {

    protected ObjectMapper objectMapper = new ObjectMapper();

    protected abstract Class<T> getClassEntity();

    @Autowired
    private ModuleLocator moduleLocator;

    @Autowired
    protected TraceService traceService;

    @PersistenceContext
    public EntityManager entityManager;

    private final int ENTITY_LIMIT = 500;

    public long getMaxCountsEntities() {
        return ENTITY_LIMIT;
    }

    public List<RepOperations> restrictedOperations(){
        return new ArrayList<>();
    }

    public void create(T object) {
        entityManager.persist(object);
    }

    public void update(T object) {
        T classicObject = read(object.getId());

        if (classicObject == null)
            return;

        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors()) {
                if (pd.getReadMethod() != null && !"class".equals(pd.getName())) {
                    Object result = pd.getReadMethod().invoke(object);
                    if (result != null) {
                        if (pd.getWriteMethod() != null)
                            pd.getWriteMethod().invoke(classicObject, result);
                    }
                }
            }
        } catch (Exception e) {
            traceService.logException("Error update entity : " + e.getMessage(), e, TraceLevel.Error,true);
            return;
        }

        entityManager.merge(classicObject);
    }

    public void merge(T object) {
        entityManager.merge(object);
    }

    public void remove(Long id) {
        remove(entityManager.find(getClassEntity(), id));
    }

    public void remove(T entity) {
        entityManager.remove(entity);
    }

    public T read(Long id) {
        T result = entityManager.find(getClassEntity(), id);
        return result;
    }

    public List<T> list() {
        List<T> list = (List<T>) entityManager.createQuery("Select t from " + getClassEntity().getSimpleName() + " t order by t.id").getResultList();
        return list;
    }

    public List<T> listPartial(int first, int max) {
        Query query = entityManager.createQuery("Select t from " + getClassEntity().getSimpleName() + " t order by t.id");
        query.setFirstResult(first);
        query.setMaxResults(max);
        List<T> list = (List<T>) query.getResultList();
        return list;
    }

    public List<T> listPartial(int first, int max, boolean isRequestApi) {
        return listPartial(first,max);
    }

    public Long count() {
        long rowCnt = (Long) entityManager.createQuery("SELECT count(a) FROM " + getClassEntity().getSimpleName() + " a").getSingleResult();
        return rowCnt;
    }

    public List<T> getEntitiesByFieldAndValue(String field, Object value) {
        List<T> list = (List<T>) entityManager.createQuery("select ent from " + getClassEntity().getSimpleName() + " ent where ent." + field + "=:value")
                .setParameter("value", value).getResultList();
        return list;
    }

    public T getSingleEntityByFieldAndValue(String field, Object value) {
        List<T> list = getEntitiesByFieldAndValue(field, value);
        return list.isEmpty() ? null : list.get(0);
    }


    public FilterQuery<T> initFilter() {
        FilterQuery<T> filterQuery = new FilterQuery<>(entityManager, getClassEntity());
        return filterQuery;
    }

    public FilterCountQuery<T> initCountFilter() {
        FilterCountQuery<T> filterQuery = new FilterCountQuery<>(entityManager, getClassEntity());
        return filterQuery;
    }

    private static Field findFieldByName(String name, Class<?> type) {
        try {
           return type.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            if (type.getSuperclass() != null) {
                return findFieldByName(name,type.getSuperclass());
            }
        }

        return null;
    }

    public AbstractEntity prepare(AbstractEntity entity, BaseRepository repository, User currentUser, List<ViewType> viewTypes) throws Exception {
        if (repository instanceof PrepareEntity) {
            properties:
            for (PropertyDescriptor pd : Introspector.getBeanInfo(entity.getClass()).getPropertyDescriptors()) {
                if (pd.getReadMethod() != null && !"class".equals(pd.getName())) {
                    Object result = pd.getReadMethod().invoke(entity);
                    if (result == null) continue;
                    if (result instanceof AbstractEntity) {
                        List<ViewType> types = null;

                        Field field = findFieldByName(pd.getName(),entity.getClass());
                        if (field == null) {
                            continue;
                        }

                        for (Annotation annotation : field.getDeclaredAnnotations()) {
                            if (annotation instanceof JsonIgnore) {
                                continue properties;
                            }

                            if (annotation instanceof ModelView) {
                                ModelView modelView = (ModelView) annotation;
                                ViewType[] typesViewTypes1 =modelView.type();
                                types = Arrays.asList(typesViewTypes1);
                            }
                        }

                        AbstractEntity abstractEntity = (AbstractEntity) result;
                        BaseRepository<AbstractEntity> repository2 = (BaseRepository<AbstractEntity>) moduleLocator.findRepository(abstractEntity.getClass());
                        if (pd.getWriteMethod() != null)
                            pd.getWriteMethod().invoke(entity, prepare(abstractEntity, repository2, currentUser, types));
                    }
                }
            }


            return ((PrepareEntity) repository).prepare(entity, currentUser, viewTypes);
        }
        return entity;
    }
}
