package com.getknowledge.platform.base.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.PrepareEntity;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import com.getknowledge.platform.exceptions.ModuleNotFound;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import com.getknowledge.platform.modules.user.User;
import com.getknowledge.platform.utils.ModuleLocator;
import org.springframework.transaction.annotation.Transactional;

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

    @JsonIgnore
    public boolean isContinueIfNotEnoughRights() {
        //Данное значение учитывается при формировании списка
        //Сущность может решать пропустить ли ее при формировании списка
        //или кинуть исключение access denied
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractEntity)) return false;

        AbstractEntity that = (AbstractEntity) o;

        return id.equals(that.id);
    }
}
