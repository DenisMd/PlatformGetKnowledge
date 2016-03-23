package com.getknowledge.modules.dictionaries.currency;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("CurrencyRepository")
public class CurrencyRepository extends BaseRepository<Currency> {
    @Override
    protected Class<Currency> getClassEntity() {
        return Currency.class;
    }
}

