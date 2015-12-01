package com.getknowledge.modules.dictionaries.city;

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
        List<City> list = entityManager.createQuery("select c from City c where c.region.id = :id and c.language.name = :name")
                .setParameter("id" , new Long((Integer)data.get("regionId")))
                .setParameter("name" , data.get("name")).getResultList();
        return list;
    }
}
