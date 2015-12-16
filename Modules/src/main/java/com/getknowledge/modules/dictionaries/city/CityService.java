package com.getknowledge.modules.dictionaries.city;

import com.getknowledge.modules.dictionaries.language.names.Languages;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.services.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service("CityService")
public class CityService extends AbstractService {
    @Autowired
    private CityRepository cityRepository;

    @Action(name = "getCitiesByRegion" , mandatoryFields = {"regionId","language"})
    public List<City> getCities(HashMap<String,Object> data) {
        Long regionId = new Long((Integer)data.get("regionId"));
        //data.get("language")
        List<City> list = entityManager.createQuery("select city from City city where city.region.id=:id and city.language.name=:name")
                .setParameter("id" , regionId)
                .setParameter("name" , Languages.Ru.name()).getResultList();
        return list;
    }
}
