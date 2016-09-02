package com.getknowledge.modules.shop.item;

import com.getknowledge.modules.dictionaries.currency.Currency;
import com.getknowledge.modules.shop.price.Price;
import com.getknowledge.modules.shop.price.PriceRepository;
import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

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

    public void updateItemPrice(Item item, Boolean free, Currency currency, BigDecimal price, Integer discount) {

        if (item == null) {
            return;
        }

        Price priceObj = item.getPrice();

        if (free != null)
            priceObj.setFree(free);

        if (free) {
            priceObj.setDiscount(0);
            priceObj.setPrice(BigDecimal.ZERO);
            priceRepository.merge(priceObj);
            return;
        }

        if (currency != null) {
            priceObj.setCurrency(currency);
        }

        if (price != null) {
            priceObj.setPrice(price);
        }

        if (discount != null) {
            priceObj.setDiscount(discount);
        }

        priceRepository.merge(priceObj);
    }
}
