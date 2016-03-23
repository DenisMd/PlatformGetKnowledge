package com.getknowledge.modules.dictionaries.currency;

import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;

@Service
public class CurrencyService extends AbstractService implements BootstrapService {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Value(value = "${currency.default}")
    private String defaultCurrency;

    @Override
    public void bootstrap(HashMap<String, Object> map) throws Exception {
        if (currencyRepository.count() == 0) {
            Currency currency = new Currency();
            currency.setName(defaultCurrency);
            currency.setValue(new BigDecimal(1.0));
            currencyRepository.create(currency);

            InputStream is = getClass().getClassLoader().getResourceAsStream("currency/currency.txt");

        }
    }

    @Override
    public BootstrapInfo getBootstrapInfo() {
        BootstrapInfo bootstrapInfo = new BootstrapInfo();
        bootstrapInfo.setOrder(1);
        bootstrapInfo.setName("Currency service");
        bootstrapInfo.setRepeat(false);
        return bootstrapInfo;
    }
}
