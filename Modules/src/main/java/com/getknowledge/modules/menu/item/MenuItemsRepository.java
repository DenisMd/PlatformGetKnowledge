package com.getknowledge.modules.menu.item;

import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.enumerations.RepOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository("MenuItemsRepository")
public class MenuItemsRepository extends BaseRepository<MenuItem> {

    @Override
    public List<RepOperations> restrictedOperations() {
        List<RepOperations> operations = new ArrayList<>();
        operations.add(RepOperations.Create);
        operations.add(RepOperations.Remove);
        return operations;
    }

    @Override
    protected Class<MenuItem> getClassEntity() {
        return MenuItem.class;
    }
}
