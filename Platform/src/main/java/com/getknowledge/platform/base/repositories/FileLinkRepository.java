package com.getknowledge.platform.base.repositories;

import com.getknowledge.platform.base.entities.AbstractEntity;

public abstract class FileLinkRepository<T extends AbstractEntity> extends BaseRepository<T> {

    abstract public String getFileLink(long id);
}
