package com.getknowledge.modules.shop.item;

import com.getknowledge.modules.shop.price.Price;
import com.getknowledge.modules.shop.price.PriceRepository;
import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("ItemRepository")
public class ItemRepository extends BaseRepository<Item> {

    @Autowired
    private PriceRepository priceRepository;

    @Override
    public void create(Item object) {
        if (object.getPrice() == null) {
            Price price = new Price();
            price.setFree(true);
            priceRepository.create(price);
            object.setPrice(price);
        }
        super.create(object);
    }

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
