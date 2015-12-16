package com.getknowledge.platform.base.repositories;

import com.getknowledge.platform.base.entities.AbstractEntity;

public abstract class PrepareRepository<T extends AbstractEntity> extends BaseRepository<T> implements PrepareEntity<T> {
}
