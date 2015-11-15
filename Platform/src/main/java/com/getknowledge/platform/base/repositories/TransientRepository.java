package com.getknowledge.platform.base.repositories;

import com.getknowledge.platform.base.entities.AbstractEntity;
import org.jsoup.Connection;

import java.util.ArrayList;
import java.util.List;

public abstract class TransientRepository<T extends AbstractEntity> extends BaseRepository<T> {

    protected List<T> list = new ArrayList<>();

    @Override
    public Long count() {
        return new Long(list.size());
    }

    @Override
    public void create(T object) {
        throw new UnsupportedOperationException("create unsupported in TransientRepository");
    }

    @Override
    public List<T> getEntitiesByFieldAndValue(String field, Object value) {
        throw new UnsupportedOperationException("getEntitiesByFieldAndValue unsupported in TransientRepository");
    }

    @Override
    public T getSingleEntityByFieldAndValue(String field, Object value) {
        throw new UnsupportedOperationException("getSingleEntityByFieldAndValue unsupported in TransientRepository");
    }

    @Override
    public List<T> list() {
        return list;
    }

    @Override
    public List<T> listPartial(int first, int max) {
        return list.subList(first , max);
    }

    @Override
    public T read(Long id) {
        return list.get(id.intValue());
    }

    @Override
    public void remove(Long id) {
        throw new UnsupportedOperationException("remove unsupported in TransientRepository");
    }

    @Override
    public void update(T object) {
        throw new UnsupportedOperationException("update unsupported in TransientRepository");
    }
}
