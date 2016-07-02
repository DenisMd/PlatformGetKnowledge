package com.getknowledge.modules.tags;

import com.getknowledge.modules.programs.tags.ProgramTag;
import com.getknowledge.platform.base.repositories.BaseRepository;

import java.util.List;

public abstract class TagRepository<T extends Tag> extends BaseRepository<T> {

    public void removeTagsFromEntity(EntityWithTags<T> entity) {
        for (T tag : entity.getTags()) {
            tag.getEntities().remove(entity);
            merge(tag);
        }

        entity.getTags().clear();
    }


    public void createTags(List<String> tags, EntityWithTags<T> entity) {
        for (String tag : tags) {
            T result = createIfNotExist(tag);
            entity.getTags().add(result);
        }
    }


    public T createIfNotExist(String tag) {
        T result = getSingleEntityByFieldAndValue("tagName" , tag.toLowerCase());

        if (result == null) {
            result =createTag();
            result.setTagName(tag.toLowerCase());
            create(result);
        }

        return result;
    }

    public abstract void removeUnusedTags();

    protected abstract T createTag();
}
