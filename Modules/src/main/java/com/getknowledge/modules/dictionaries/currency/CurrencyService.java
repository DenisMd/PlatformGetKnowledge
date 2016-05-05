package com.getknowledge.modules.dictionaries.currency;

import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoService;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.modules.Result;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

@Service("CurrencyService")
public class CurrencyService extends AbstractService implements BootstrapService {

    private final String cbrUrl = "http://www.cbr.ru/scripts/XML_daily.asp";

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private TraceService trace;

    @Value(value = "${currency.default}")
    private String defaultCurrency;

    @Autowired
    private UserInfoService userInfoService;

    private void parseXmlFromCbr(boolean create) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new URL(cbrUrl).openStream());
        doc.normalizeDocument();
        NodeList valutes = doc.getElementsByTagName("Valute");
        for (int i = 0; i < valutes.getLength(); i++) {
            Node valute = valutes.item(i);
            if (valute.getNodeType() == Node.ELEMENT_NODE) {
                Element valuteElement = (Element)valute;
                String name = valuteElement.getElementsByTagName("Name").item(0).getTextContent();
                String charCode = valuteElement.getElementsByTagName("CharCode").item(0).getTextContent();
                String value = valuteElement.getElementsByTagName("Value").item(0).getTextContent();
                NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
                Number number = format.parse(value);
                double valueDouble = number.doubleValue();
                if (create) {
                    currencyRepository.createCurrency(charCode,name,valueDouble,false);
                } else {
                    currencyRepository.updateCurrency(charCode,valueDouble);
                }
            }
        }
    }

    @Override
    public void bootstrap(HashMap<String, Object> map) throws Exception {
        if (currencyRepository.count() == 0) {
            currencyRepository.createCurrency(defaultCurrency,"Российски рубль",1.0,true);
        }

        parseXmlFromCbr(true);
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
    @Transactional
    public Result adminUpdateCurrency(HashMap<String,Object> data) {

        UserInfo userInfo = userInfoService.getAuthorizedUser(data);

        if (userInfo == null) return Result.AccessDenied();
        if (!isAccessToEdit(data,new Currency())) return Result.AccessDenied();

        updateCurrency();

        return  Result.Complete();
    }

    //every day - обновлять курсы валют
    @Scheduled(cron = "0 1 1 * * ?")
    @Transactional
    public void updateCurrency(){
        try {
            parseXmlFromCbr(false);
        } catch (Exception e) {
            trace.logException("Error update currency : " + e.getMessage(),e,TraceLevel.Error);
        }
    }
}
