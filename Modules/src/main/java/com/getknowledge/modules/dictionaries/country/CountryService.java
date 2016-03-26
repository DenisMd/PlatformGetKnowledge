package com.getknowledge.modules.dictionaries.country;

import com.getknowledge.modules.dictionaries.city.City;
import com.getknowledge.modules.dictionaries.city.CityRepository;
import com.getknowledge.modules.dictionaries.language.LanguageRepository;
import com.getknowledge.modules.dictionaries.region.Region;
import com.getknowledge.modules.dictionaries.region.RegionRepository;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashMap;

@Service("CountryService")
public class CountryService extends AbstractService implements BootstrapService {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private CityRepository cityRepository;

    @Override
    public void bootstrap(HashMap<String, Object> map) throws Exception {
        if (countryRepository.count() == 0) {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("com.getknowledge.modules/countries/counriesBootstrap.xml");
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList nodeList = document.getElementsByTagName("country");
            HashMap<String,Country> countries = new HashMap<>();
            for (int i=0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String id = element.getElementsByTagName("country_id").item(0).getTextContent();
                    String name = element.getElementsByTagName("name").item(0).getTextContent();
                    Country country = countryRepository.createCountry(name);
                    countries.put(id,country);
                }
            }

            nodeList = document.getElementsByTagName("region");
            HashMap<String,Region> regions = new HashMap<>();
            for (int i=0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    String id = element.getElementsByTagName("region_id").item(0).getTextContent();
                    String countryId = element.getElementsByTagName("country_id").item(0).getTextContent();
                    String name = element.getElementsByTagName("name").item(0).getTextContent();

                    Region region = regionRepository.create(name,countries.get(countryId));
                    regions.put(id,region);
                }
            }

            nodeList = document.getElementsByTagName("city");
            for (int i=0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String regionId = element.getElementsByTagName("region_id").item(0).getTextContent();
                    String name = element.getElementsByTagName("name").item(0).getTextContent();
                    cityRepository.create(name,regions.get(regionId));
                }
            }
        }

    }

    @Override
    public BootstrapInfo getBootstrapInfo() {
        BootstrapInfo bootstrapInfo = new BootstrapInfo();
        bootstrapInfo.setName("Country service");
        bootstrapInfo.setOrder(1);
        return  bootstrapInfo;
    }

}
