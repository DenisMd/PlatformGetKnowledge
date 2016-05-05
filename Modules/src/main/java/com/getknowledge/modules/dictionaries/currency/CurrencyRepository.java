package com.getknowledge.modules.dictionaries.currency;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository("CurrencyRepository")
public class CurrencyRepository extends BaseRepository<Currency> {
    @Override
    protected Class<Currency> getClassEntity() {
        return Currency.class;
    }

    @Override
    public void create(Currency object, boolean fromRequestApi) {
        if (fromRequestApi) {
            throw new RuntimeException("Error create currency from request api");
        }
        super.create(object, fromRequestApi);
    }

    @Override
    public void update(Currency object, boolean fromRequestApi) {
        if (fromRequestApi) {
            throw new RuntimeException("Error update currency from request api");
        }
        super.update(object, fromRequestApi);
    }

    @Override
    public void remove(Long id, boolean fromRequestApi) {
        if (fromRequestApi) {
            throw new RuntimeException("Error remove currency from request api");
        }
        super.remove(id, fromRequestApi);
    }

    public void createCurrency(String charCode,String name,double value,boolean isBase){
        Currency currency = new Currency();
        currency.setBaseCurrency(isBase);
        currency.setCharCode(charCode);
        currency.setName(name);
        currency.setValue(new BigDecimal(value));
        create(currency);
    }

    public void updateCurrency(String charCode, double value){
        Currency currency = getSingleEntityByFieldAndValue("charCode",charCode);

        if (currency != null) {
            currency.setValue(new BigDecimal(value));
            merge(currency);
        }
    }

}

