package com.getknowledge.modules.shop.item;

import com.getknowledge.modules.shop.price.PriceRepository;
import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class ItemRepository extends BaseRepository<Item> {

    @Autowired
    private PriceRepository priceRepository;

    @Override
    protected Class<Item> getClassEntity() {
        return Item.class;
    }

    @Override
    public void remove(Item entity) {
        priceRepository.remove(entity.getPrice());
        super.remove(entity);
    }
}
