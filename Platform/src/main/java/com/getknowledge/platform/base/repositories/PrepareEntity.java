package com.getknowledge.platform.base.repositories;

import com.getknowledge.platform.base.entities.AbstractEntity;

public interface PrepareEntity<T extends AbstractEntity> {
    public T prepare(T entity);
}
