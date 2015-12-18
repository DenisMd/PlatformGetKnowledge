package com.getknowledge.modules.dictionaries.region;

import com.getknowledge.modules.dictionaries.country.CountryRepository;
import com.getknowledge.modules.dictionaries.language.names.Languages;
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

    @Action(name = "chooseRegionsByCountry" ,mandatoryFields = {"countryId" })
    public List<Region> chooseRegionsByCountry(HashMap<String,Object> data) {
        Long countryLong = new Long((Integer) data.get("countryId"));
        List<Region> regionList = entityManager.createQuery("select r from Region r where r.country.id=:id")
                .setParameter("id" , countryLong).getResultList();
        return regionList;
    }
}
