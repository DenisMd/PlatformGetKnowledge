package com.getknowledge.modules.dictionaries.region;

import com.getknowledge.modules.dictionaries.country.CountryRepository;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.services.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service("RegionService")
public class RegionService extends AbstractService {


    @Autowired
    private CountryRepository countryRepository;

    @Action(name = "chooseRegionsByCountry" ,mandatoryFields = {"countryId" , "language"})
    public List<Region> chooseRegionsByCountry(HashMap<String,Object> data) {
        Long countryLong = (Long) data.get("countryId");
        List<Region> regionList = entityManager.createQuery("select r from Region r where r.country.id=:id and r.language.name=:languageName")
                .setParameter("id" , countryLong)
                .setParameter("languageName",data.get("language")).getResultList();
        return regionList;
    }
}
