package com.getknowledge.modules.section;

import com.getknowledge.modules.menu.item.MenuItem;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.PrepareRepository;
import org.springframework.stereotype.Repository;

@Repository("SectionRepository")
public class SectionRepository extends PrepareRepository<Section> {

    @Override
    public Section prepare(Section entity) {
        if (entity == null || entity.getMenuItem() == null) return entity;

        for (MenuItem menuItem : entity.getMenuItem().getSubItems()) {
            menuItem.setSubItems(null);
        }

        return entity;
    }
}
