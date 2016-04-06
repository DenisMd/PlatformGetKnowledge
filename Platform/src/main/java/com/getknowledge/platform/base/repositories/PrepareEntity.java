package com.getknowledge.platform.base.repositories;

import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.modules.user.User;

public interface PrepareEntity<T extends AbstractEntity> {
    T prepare(T entity,User currentUser);
}
