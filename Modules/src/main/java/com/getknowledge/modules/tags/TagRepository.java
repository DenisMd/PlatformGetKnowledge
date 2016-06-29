package com.getknowledge.modules.tags;

import com.getknowledge.modules.programs.tags.ProgramTag;
import com.getknowledge.platform.base.entities.AbstractEntity;
import com.getknowledge.platform.base.repositories.BaseRepository;

import java.util.AbstractCollection;
import java.util.List;

public abstract class TagRepository<T extends Tag> extends BaseRepository<T> {

    public void removeTagsFromEntity(EntityWithTag<T> entity) {
        for (T tag : entity.getTags()) {
            tag.getEntities().remove(entity);
            merge(programTag);
        }

        entity.getTags().clear();
    }


    public void createTags(List<String> tags, EntityWithTags<ProgramTag> entity) {
        for (String tag : tags) {
            ProgramTag programTag = createIfNotExist(tag);
            entity.getTags().add(programTag);
        }
    }


    public ProgramTag createIfNotExist(String tag) {
        ProgramTag result = getSingleEntityByFieldAndValue("tagName" , tag.toLowerCase());

        if (result == null) {
            result = new ProgramTag();
            result.setTagName(tag.toLowerCase());
            create(result);
        }

        return result;
    }

    public void removeUnusedTags() {
        List<ProgramTag> list = entityManager.createQuery("select t from ProgramTag  t where t.programs is empty").getResultList();
        list.forEach((tag -> {
            remove(tag.getId());
        }));
    }
}
