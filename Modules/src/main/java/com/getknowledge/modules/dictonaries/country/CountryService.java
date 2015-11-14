package com.getknowledge.modules.dictonaries.country;

import com.getknowledge.modules.dictonaries.language.Language;
import com.getknowledge.modules.dictonaries.language.LanguageRepository;
import com.getknowledge.modules.dictonaries.language.names.Languages;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Properties;

@Service("CountryService")
public class CountryService extends AbstractService implements BootstrapService {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Override
    public void bootstrap(HashMap<String, Object> map) throws Exception {
        if (countryRepository.count(Country.class) == 0) {
            Language languageEn = languageRepository.getSingleEntityByFieldAndValue(Language.class , "name" , Languages.En.name());
            Language languageRu = languageRepository.getSingleEntityByFieldAndValue(Language.class , "name" , Languages.Ru.name());

            Properties properties = new Properties();
            properties.load(getClass().getClassLoader().getResourceAsStream("com.getknowledge.modules/dictonaries/country/en"));

            for (Object key : properties.keySet()) {
                String code = key.toString();
                String countryName=  properties.getProperty(key.toString());
                Country country = new Country();
                country.setCode(code);
                country.setName(countryName);
                country.setLanguage(languageEn);
                countryRepository.create(country);
            }

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("com.getknowledge.modules/dictonaries/country/ru");
            Reader reader = new InputStreamReader(inputStream, "UTF-8");
            properties.load(reader);
            for (Object key : properties.keySet()) {
                String code = key.toString();
                String countryName=  properties.getProperty(key.toString());
                Country country = new Country();
                country.setCode(code);
                country.setName(countryName);
                country.setLanguage(languageRu);
                countryRepository.create(country);
            }
        }
    }

    @Override
    public BootstrapInfo getBootstrapInfo() {
        BootstrapInfo bootstrapInfo = new BootstrapInfo();
        bootstrapInfo.setName("CountryService");
        bootstrapInfo.setOrder(1);
        return  bootstrapInfo;
    }
}
