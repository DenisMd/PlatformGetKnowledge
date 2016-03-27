package com.getknowledge.platform.base.repositories;

import com.getknowledge.platform.base.entities.EntityWithTags;
import com.getknowledge.platform.base.entities.ITag;

import java.util.List;

public interface ITagRepository<T extends ITag> {
    public T createIfNotExist(String tagName);
    public void removeUnusedTags();
    public void removeTagsFromEntity(EntityWithTags<T> entity);
    public void createTags(List<String> tags , EntityWithTags<T> entity);
}

