package com.getknowledge.platform.base.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.PrepareEntity;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import com.getknowledge.platform.exceptions.ModuleNotFound;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import com.getknowledge.platform.modules.user.User;
import com.getknowledge.platform.utils.ModuleLocator;

import javax.persistence.*;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.security.Principal;
import java.util.List;

@MappedSuperclass
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @JsonIgnore
    public abstract AuthorizationList getAuthorizationList();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractEntity)) return false;

        AbstractEntity that = (AbstractEntity) o;

        return id.equals(that.id);

    }

    public static AbstractEntity prepare (AbstractEntity entity, BaseRepository repository, User currentUser, ModuleLocator moduleLocator) throws Exception {
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
                        pd.getWriteMethod().invoke(entity, prepare(abstractEntity,repository2,currentUser,moduleLocator));
                    }
                }
            }


            return ((PrepareEntity) repository).prepare(entity);
        }
        return entity;
    }
}
