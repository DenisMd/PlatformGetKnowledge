package com.getknowledge.modules.shop.price;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("PriceRepository")
public class PriceRepository extends BaseRepository<Price> {
    @Override
    protected Class<Price> getClassEntity() {
        return Price.class;
    }
}
