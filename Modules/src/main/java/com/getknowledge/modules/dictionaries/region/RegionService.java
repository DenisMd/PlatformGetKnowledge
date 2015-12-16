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

    @Action(name = "chooseRegionsByCountry" ,mandatoryFields = {"countryId" , "language"})
    public List<Region> chooseRegionsByCountry(HashMap<String,Object> data) {
        Long countryLong = new Long((Integer) data.get("countryId"));
        //data.get("language")
        List<Region> regionList = entityManager.createQuery("select r from Region r where r.country.id=:id and r.language.name=:languageName")
                .setParameter("id" , countryLong)
                .setParameter("languageName", Languages.Ru.name()).getResultList();
        return regionList;
    }
}
