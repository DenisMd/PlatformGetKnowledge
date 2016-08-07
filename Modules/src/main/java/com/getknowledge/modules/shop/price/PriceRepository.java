package com.getknowledge.modules.shop.price;

import com.getknowledge.modules.dictionaries.currency.CurrencyRepository;
import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("PriceRepository")
public class PriceRepository extends BaseRepository<Price> {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Override
    protected Class<Price> getClassEntity() {
        return Price.class;
    }

    @Override
    public void create(Price object) {
        if (object.getCurrency() == null) {
            object.setCurrency(currencyRepository.getBaseCurrency());
        }
        super.create(object);
    }
}
