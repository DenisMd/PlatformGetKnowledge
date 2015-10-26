package com.getknowledge.platform.base.repositories;

import com.getknowledge.platform.base.entities.AbstractEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

public abstract class PrepareRepository<T extends AbstractEntity> extends BaseRepository<T> implements PrepareEntity<T> {
    @Override
    public T read(Long id, Class<T> classEntity) {
        return prepare(super.read(id, classEntity));
    }

    @Override
    public List<T> list(Class<T> classEntity) {
        return super.list(classEntity).stream().map(this::prepare).collect(Collectors.toList());
    }

    @Override
    public List<T> listPartial(Class<T> classEntity, int first, int max) {
        return super.listPartial(classEntity, first, max).stream().map(this::prepare).collect(Collectors.toList());
    }

    @Override
    public List<T> getEntitiesByFieldAndValue(Class<T> classEntity ,String field, Object value) {
        return super.getEntitiesByFieldAndValue(classEntity , field,value).stream().map(this::prepare).collect(Collectors.toList());
    }
}
