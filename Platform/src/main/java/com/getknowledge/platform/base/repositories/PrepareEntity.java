package com.getknowledge.platform.base.repositories;

import com.getknowledge.platform.base.entities.AbstractEntity;

public interface PrepareEntity<T extends AbstractEntity> {
    T prepare(T entity);
}
