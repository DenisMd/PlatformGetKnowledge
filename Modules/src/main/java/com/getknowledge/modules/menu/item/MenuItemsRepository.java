package com.getknowledge.modules.menu.item;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("MenuItemsRepository")
public class MenuItemsRepository extends BaseRepository<MenuItem> {

    @Override
    protected Class<MenuItem> getClassEntity() {
        return MenuItem.class;
    }
}
