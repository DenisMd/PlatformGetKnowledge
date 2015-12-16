package com.getknowledge.modules.menu;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("MenuRepository")
public class MenuRepository extends BaseRepository<Menu> {
    @Override
    protected Class<Menu> getClassEntity() {
        return Menu.class;
    }
}
