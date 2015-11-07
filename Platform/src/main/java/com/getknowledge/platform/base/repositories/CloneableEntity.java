package com.getknowledge.platform.base.repositories;

import com.getknowledge.platform.base.entities.AbstractEntity;

public interface CloneableEntity<T extends AbstractEntity> {
    public T clone (T entity);
}
