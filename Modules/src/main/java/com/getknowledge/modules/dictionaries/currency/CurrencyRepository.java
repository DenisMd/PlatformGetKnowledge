package com.getknowledge.modules.dictionaries.currency;

import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.enumerations.RepOperations;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository("CurrencyRepository")
public class CurrencyRepository extends BaseRepository<Currency> {

    @Override
    public List<RepOperations> restrictedOperations() {
        List<RepOperations> operations = new ArrayList<>();
        operations.add(RepOperations.Create);
        operations.add(RepOperations.Remove);
        return operations;
    }

    @Override
    protected Class<Currency> getClassEntity() {
        return Currency.class;
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

    public Currency getBaseCurrency() {
        return getSingleEntityByFieldAndValue("baseCurrency" , true);
    }

}

