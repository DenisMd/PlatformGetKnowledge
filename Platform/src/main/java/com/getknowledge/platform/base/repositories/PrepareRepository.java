package com.getknowledge.platform.base.repositories;

import com.getknowledge.platform.base.entities.AbstractEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

public abstract class PrepareRepository<T extends AbstractEntity> extends BaseRepository<T> implements PrepareEntity<T>, CloneableEntity<T> {
}
