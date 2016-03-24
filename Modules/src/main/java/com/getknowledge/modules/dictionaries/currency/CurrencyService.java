package com.getknowledge.modules.dictionaries.currency;

import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoService;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.modules.Result;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import com.getknowledge.platform.modules.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

@Service
public class CurrencyService extends AbstractService implements BootstrapService {

    @Autowired
    private CurrencyRepository currencyRepository;


    @Autowired
    private TraceService trace;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserRepository userRepository;

    @Value(value = "${currency.default}")
    private String defaultCurrency;

    @Override
    public void bootstrap(HashMap<String, Object> map) throws Exception {
        if (currencyRepository.count() == 0) {
            Currency defaultCur = new Currency();
            defaultCur.setName(defaultCurrency);
            defaultCur.setValue(new BigDecimal(1.0));
            currencyRepository.create(defaultCur);

            InputStream is = getClass().getClassLoader().getResourceAsStream("currency/currency.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String currencyName = "";
            while ( (currencyName = reader.readLine()) != null) {
                Currency currency = new Currency();
                currency.setName(currencyName);
                currency.setValue(new BigDecimal(Math.random()));
                currencyRepository.create(currency);
            }
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

    @Action(name = "updateCurrency")
    public Result adminUpdateCurrency(HashMap<String,Object> data) {

        UserInfo userInfo = userInfoService.getAuthorizedUser(data);

        if (userInfo == null) return Result.AccessDenied();
        if (!isAccessToEdit(data,new Currency(),userRepository)) return Result.AccessDenied();

        updateCurrency();

        return  Result.Complete();
    }

    //every day - обновлять курсы валют
    @Scheduled(cron = "0 1 1 * * ?")
    public void updateCurrency(){
        List<Currency> currencyList =  entityManager.createQuery("select  cur from Currency  cur where cur.baseCurrency = false").getResultList();
        trace.log("Start update currency" , TraceLevel.Event);
        for (Currency currency : currencyList) {
            currency.setValue(new BigDecimal(Math.random() * 100));
            currencyRepository.merge(currency);
        }
    }
}
