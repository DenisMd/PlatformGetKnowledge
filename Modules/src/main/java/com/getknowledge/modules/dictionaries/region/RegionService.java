package com.getknowledge.modules.dictionaries.region;

import com.getknowledge.modules.dictionaries.country.Country;
import com.getknowledge.modules.dictionaries.country.CountryRepository;
import com.getknowledge.modules.dictionaries.language.names.Languages;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.services.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service("RegionService")
public class RegionService extends AbstractService {


    @Autowired
    private CountryRepository countryRepository;

    @Action(name = "getRegionsByCountry" ,mandatoryFields = {"countryId" })
    @Transactional
    public List<Region> chooseRegionsByCountry(HashMap<String,Object> data) {
        Long countryLong = longFromField("countryId",data);
        Country country = countryRepository.read(countryLong);
        return country == null ? null : country.getRegions();
    }
}
