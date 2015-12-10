package com.getknowledge.platform.base.repositories;

import com.getknowledge.platform.base.entities.AbstractEntity;

public abstract class CloneableEntity<T extends AbstractEntity> extends AbstractEntity {
    public abstract T clone ();
}
