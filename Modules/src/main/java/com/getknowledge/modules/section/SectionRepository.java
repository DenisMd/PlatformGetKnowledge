package com.getknowledge.modules.section;

import com.getknowledge.modules.menu.item.MenuItem;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.PrepareRepository;
import org.springframework.stereotype.Repository;

@Repository("SectionRepository")
public class SectionRepository extends PrepareRepository<Section> {

    @Override
    protected Class<Section> getClassEntity() {
        return Section.class;
    }

    @Override
    public Section clone(Section entity) {
        Section section = new Section();
        section.setCover(entity.getCover());
        section.setDescription(entity.getDescription());
        section.setMenuItem(entity.getMenuItem());
        section.setTitle(entity.getTitle());
        return section;
    }

    @Override
    public Section prepare(Section entity) {
        if (entity == null || entity.getMenuItem() == null) return entity;

        for (MenuItem menuItem : entity.getMenuItem().getSubItems()) {
            menuItem.setSubItems(null);
        }

        return entity;
    }
}
